package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.StudentDto;
import kc.ebenezer.model.Gender;
import kc.ebenezer.model.Student;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class StudentMapper extends BaseMapper<Student, StudentDto> implements Mapper<Student, StudentDto> {
    @Inject
    private StudentTeamMapper studentTeamMapper;
    @Inject
    private AttendanceRecordMapper attendanceRecordMapper;
    @Inject
    private ImageCollectionMapper imageCollectionMapper;

    @Override
    protected String[] getIgnoreProperties() {
        return new String[] {"gender", "studentTeam", "projectId", "project", "attendanceSnapshot", "imageCollection"};
    }

    @Override
    public Student toModel(StudentDto dto) {
        if (dto == null) {
            return null;
        }

        Student model = super.toModel(dto);

        Gender gender = null;
        if (dto.getGender() != null) {
            gender = Gender.fromName(dto.getGender());
            if (gender == null) {
                gender = Gender.fromCode(dto.getGender());
            }
        }
        model.setGender(gender);
        model.setAttendanceSnapshot(attendanceRecordMapper.toModel(dto.getAttendanceSnapshot()));
        model.setImageCollection(imageCollectionMapper.toModel(dto.getImageCollection()));
        return model;
    }

    @Override
    public StudentDto toDto(Student model) {
        if (model == null) {
            return null;
        }

        StudentDto dto = super.toDto(model);

        String gender = null;
        if (model.getGender() != null) {
            gender = model.getGender().getDescription();
        }
        dto.setGender(gender);
        dto.setStudentTeam(studentTeamMapper.toDtoShallow(model.getStudentTeam()));
        if (model.getProject() != null) {
            dto.setProjectId(model.getProject().getId());
        }
        dto.setAttendanceSnapshot(attendanceRecordMapper.toDtoOmitStudent(model.getAttendanceSnapshot()));
        dto.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));

        return dto;
    }

    public StudentDto toDtoShallow(Student model) {
        if (model == null) {
            return null;
        }

        StudentDto dto = super.toDto(model);

        String gender = null;
        if (model.getGender() != null) {
            gender = model.getGender().getDescription();
        }
        dto.setGender(gender);
        dto.setStudentTeam(studentTeamMapper.toDtoShallowNoTeamMembersNoLeaders(model.getStudentTeam()));
        if (model.getProject() != null) {
            dto.setProjectId(model.getProject().getId());
        }
        dto.setAttendanceSnapshot(attendanceRecordMapper.toDtoOmitStudent(model.getAttendanceSnapshot()));
        dto.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));

        return dto;
    }


    @Override
    protected Student constructModel() {
        return new Student();
    }

    @Override
    protected StudentDto constructDto() {
        return new StudentDto();
    }
}

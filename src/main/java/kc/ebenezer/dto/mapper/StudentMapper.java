package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.StudentDto;
import kc.ebenezer.model.Gender;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.Student;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.service.ProjectPermissionService;
import kc.ebenezer.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class StudentMapper extends BaseMapper<Student, StudentDto> implements Mapper<Student, StudentDto> {
    @Inject
    private StudentTeamMapper studentTeamMapper;
    @Inject
    private AttendanceRecordMapper attendanceRecordMapper;
    @Inject
    private ImageCollectionMapper imageCollectionMapper;
    @Inject
    private UserService userService;
    @Inject
    private ProjectPermissionService projectPermissionService;

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

    public List<StudentDto> toDtoShallow(List<Student> modelList) {
        if (modelList == null) {
            return new ArrayList<>();
        }

        List<StudentDto> result = new ArrayList<>(modelList.size());
        for (Student model : modelList) {
            result.add(toDtoShallow(model));
        }
        return result;
    }

    private void checkAndClearPrivateData(Project project, StudentDto dto) {
        if (!userService.getCurrentUser().isPresent() ||
            !projectPermissionService.userHasPermission(userService.getCurrentUser().get(), project, ProjectPermission.VIEW_STUDENTS)) {

            dto.setGender(null);
            dto.setAge(null);
            dto.setContactName(null);
            dto.setContactRelationship(null);
            dto.setEmail(null);
            dto.setPhone(null);
            dto.setSchool(null);
            dto.setSchoolYear(null);
            dto.setSpecialInstructions(null);
        }
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

        checkAndClearPrivateData(model.getProject(), dto);

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

        checkAndClearPrivateData(model.getProject(), dto);

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

package ebenezer.dto.mapper;

import ebenezer.dto.StudentDto;
import ebenezer.model.Gender;
import ebenezer.model.Student;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class StudentMapper extends BaseMapper<Student, StudentDto> implements Mapper<Student, StudentDto> {
    @Inject
    private StudentTeamMapper studentTeamMapper;

    @Override
    protected String[] getIgnoreProperties() {
        return new String[] {"gender", "studentTeam", "projectId", "project"};
    }

    @Override
    public Student toModel(StudentDto dto) {
        Student model = super.toModel(dto);

        Gender gender = null;
        if (dto.getGender() != null) {
            gender = Gender.fromName(dto.getGender());
            if (gender == null) {
                gender = Gender.fromCode(dto.getGender());
            }
        }
        model.setGender(gender);
        return model;
    }

    @Override
    public StudentDto toDto(Student model) {
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

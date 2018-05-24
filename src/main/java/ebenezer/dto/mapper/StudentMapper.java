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
    public Student toModel(StudentDto dto) {
        Gender gender = null;
        if (dto.getGender() != null) {
            gender = Gender.fromName(dto.getGender());
            if (gender == null) {
                gender = Gender.fromCode(dto.getGender());
            }
        }

        return new Student(
                dto.getId(),
                dto.getName(),
                dto.getGivenName(),
                dto.getFamilyName(),
                dto.getMediaDescriptor(),
                dto.getContactName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getSchool(),
                dto.getAge(),
                dto.getSchoolYear(),
                gender,
                dto.getSpecialInstructions(),
                null,
                null
        );
    }

    @Override
    public StudentDto toDto(Student model) {
        String gender = null;
        if (model.getGender() != null) {
            gender = model.getGender().getDescription();
        }
        return new StudentDto(
                model.getId(),
                model.getName(),
                model.getGivenName(),
                model.getFamilyName(),
                model.getMediaDescriptor(),
                model.getContactName(),
                model.getEmail(),
                model.getPhone(),
                model.getSchool(),
                model.getAge(),
                gender,
                model.getSpecialInstructions(),
                model.getSchoolYear(),
                model.getProject().getId(),
                studentTeamMapper.toDtoShallow(model.getStudentTeam()),
                model.getCreated(),
                model.getUpdated());
    }
}

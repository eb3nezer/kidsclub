package ebenezer.dto.mapper;

import ebenezer.dto.ProjectDto;
import ebenezer.dto.StudentDto;
import ebenezer.dto.StudentTeamDto;
import ebenezer.dto.UserDto;
import ebenezer.model.*;
import ebenezer.permissions.SitePermission;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class StudentMapper extends BaseMapper<Student, StudentDto> implements Mapper<Student, StudentDto> {
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private StudentTeamMapper studentTeamMapper;

    @Override
    public Student toModel(StudentDto dto) {
        return new Student(
                dto.getId(),
                dto.getName(),
                dto.getGivenName(),
                dto.getFamilyName(),
                dto.getAvatarUrl(),
                dto.getMediaDescriptor(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getSchool(),
                dto.getAge(),
                dto.getSchoolYear(),
                null,
                null
        );
    }

    @Override
    public StudentDto toDto(Student model) {
        return new StudentDto(
                model.getId(),
                model.getName(),
                model.getGivenName(),
                model.getFamilyName(),
                model.getAvatarUrl(),
                model.getMediaDescriptor(),
                model.getEmail(),
                model.getPhone(),
                model.getSchool(),
                model.getAge(),
                model.getSchoolYear(),
                model.getProject().getId(),
                studentTeamMapper.toDtoShallow(model.getStudentTeam()),
                model.getCreated(),
                model.getUpdated());
    }
}

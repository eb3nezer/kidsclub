package ebenezer.dto.mapper;

import ebenezer.dto.StudentDto;
import ebenezer.dto.StudentTeamDto;
import ebenezer.model.Student;
import ebenezer.model.StudentTeam;
import ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class StudentTeamMapper extends BaseMapper<StudentTeam, StudentTeamDto> implements Mapper<StudentTeam, StudentTeamDto> {
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private UserMapper userMapper;
    @Inject
    private StudentMapper studentMapper;

    @Override
    public StudentTeam toModel(StudentTeamDto dto) {
        return new StudentTeam(
                projectMapper.toModel(dto.getProject()),
                dto.getName(),
                dto.getScore(),
                new HashSet<>(userMapper.toModel(dto.getLeaders())),
                new HashSet<>(studentMapper.toModel(dto.getStudents())),
                dto.getAvatarUrl(),
                dto.getMediaDescriptor(),
                0);
    }

    @Override
    public StudentTeamDto toDto(StudentTeam model) {
        List<Student> students = new ArrayList<>(model.getStudents());
        students.sort(new Student.StudentComparator());
        List<User> leaders = new ArrayList<>(model.getLeaders());
        leaders.sort(new User.UserComparator());

        return new StudentTeamDto(
                model.getId(),
                projectMapper.toDto(model.getProject()),
                model.getName(),
                model.getScore(),
                userMapper.toDto(leaders),
                studentMapper.toDto(students),
                model.getAvatarUrl(),
                model.getMediaDescriptor(),
                model.getCreated().getTime(),
                model.getUpdated().getTime());
    }

    @Override
    protected StudentTeam constructModel() {
        return new StudentTeam();
    }

    @Override
    protected StudentTeamDto constructDto() {
        return new StudentTeamDto();
    }

    public StudentTeamDto toDtoShallow(StudentTeam model) {
        if (model == null) {
            return null;
        }
        List<StudentDto> students = new ArrayList<>();
        if (model.getStudents() != null) {
            for (Student student : model.getStudents()) {
                StudentDto studentDto = new StudentDto();
                studentDto.setId(student.getId());
                studentDto.setName(student.getName());
                students.add(studentDto);
            }
        }
        return new StudentTeamDto(
                model.getId(),
                projectMapper.toDto(model.getProject()),
                model.getName(),
                model.getScore(),
                userMapper.toDto(new ArrayList(model.getLeaders())),
                students,
                model.getAvatarUrl(),
                model.getMediaDescriptor(),
                model.getCreated().getTime(),
                model.getUpdated().getTime());
    }
}

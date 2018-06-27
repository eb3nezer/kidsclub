package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.StudentDto;
import kc.ebenezer.dto.StudentTeamDto;
import kc.ebenezer.model.Student;
import kc.ebenezer.model.StudentTeam;
import kc.ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
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
    @Inject
    private ImageCollectionMapper imageCollectionMapper;

    @Override
    protected String[] getIgnoreProperties() {
        return new String[] {"project", "leaders", "students", "imageCollection", "created", "updated"};
    }

    @Override
    public StudentTeam toModel(StudentTeamDto dto) {
        if (dto == null) {
            return null;
        }
        StudentTeam model = super.toModel(dto);
        model.setProject(projectMapper.toModel(dto.getProject()));
        model.setLeaders(new HashSet<>(userMapper.toModel(dto.getLeaders())));
        model.setStudents(new HashSet<>(studentMapper.toModel(dto.getStudents())));
        model.setImageCollection(imageCollectionMapper.toModel(dto.getImageCollection()));

        return model;
    }

    @Override
    public StudentTeamDto toDto(StudentTeam model) {
        if (model == null) {
            return null;
        }
        StudentTeamDto dto = super.toDto(model);

        List<Student> students = new ArrayList<>(model.getStudents());
        students.sort(new Student.StudentComparator());
        List<User> leaders = new ArrayList<>(model.getLeaders());
        leaders.sort(new User.UserComparator());

        dto.setStudents(studentMapper.toDto(students));
        dto.setLeaders(userMapper.toDto(leaders));
        dto.setProject(projectMapper.toDto(model.getProject()));
        dto.setCreated(model.getCreated().getTime());
        dto.setUpdated(model.getUpdated().getTime());
        dto.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));

        return dto;
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
        StudentTeamDto dto = super.toDto(model);

        List<StudentDto> students = new ArrayList<>();
        if (model.getStudents() != null) {
            for (Student student : model.getStudents()) {
                StudentDto studentDto = new StudentDto();
                studentDto.setId(student.getId());
                studentDto.setName(student.getName());
                students.add(studentDto);
            }
        }

        dto.setStudents(students);
        dto.setProject(projectMapper.toDto(model.getProject()));
        dto.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));
        dto.setLeaders(userMapper.toDto(new ArrayList<>(model.getLeaders())));
        dto.setCreated(model.getCreated().getTime());
        dto.setUpdated(model.getUpdated().getTime());

        return dto;
    }

    public StudentTeamDto toDtoShallowNoTeamMembersNoLeaders(StudentTeam model) {
        if (model == null) {
            return null;
        }
        StudentTeamDto dto = super.toDto(model);
        dto.setProject(projectMapper.toDtoNoUsers(model.getProject()));
        dto.setCreated(model.getCreated().getTime());
        dto.setUpdated(model.getUpdated().getTime());

        return dto;
    }
}

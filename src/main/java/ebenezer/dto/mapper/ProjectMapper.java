package ebenezer.dto.mapper;

import ebenezer.dto.ProjectDto;
import ebenezer.dto.UserDto;
import ebenezer.model.Project;
import ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectMapper extends BaseMapper<Project, ProjectDto> implements Mapper<Project, ProjectDto> {
    @Inject
    private UserMapper userMapper;

    @Override
    public Project toModel(ProjectDto dto) {
        if (dto == null) {
            return null;
        }
        return new Project(dto.getId(), dto.getName());
    }

    @Override
    public ProjectDto toDto(Project model) {
        if (model == null) {
            return null;
        }
        List<User> users = new ArrayList<>(model.getUsers());
        users.sort(new User.UserComparator());
        List<UserDto> projectUsers = userMapper.toDto(users);
        return new ProjectDto(model.getId(), model.getName(), projectUsers);
    }

    public ProjectDto toDtoNoUsers(Project model) {
        if (model == null) {
            return null;
        }
        List<UserDto> projectUsers = new ArrayList<>();
        return new ProjectDto(model.getId(), model.getName(), projectUsers);
    }
}

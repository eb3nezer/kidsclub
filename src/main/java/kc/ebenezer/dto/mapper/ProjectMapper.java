package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.ProjectDto;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.ProjectProperty;
import kc.ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

@Component
public class ProjectMapper extends BaseMapper<Project, ProjectDto> implements Mapper<Project, ProjectDto> {
    @Inject
    private UserMapper userMapper;

    @Override
    public Project toModel(ProjectDto dto) {
        if (dto == null) {
            return null;
        }
        Project model = super.toModel(dto);
        Set<ProjectProperty> projectProperties = new HashSet<>();
        if (dto.getProperties() != null) {
            for (Map.Entry<String, String> entry : dto.getProperties().entrySet()) {
                ProjectProperty projectProperty = new ProjectProperty(model, entry.getKey(), entry.getValue());
                projectProperties.add(projectProperty);
            }
        }
        model.setProjectProperties(projectProperties);

        return model;
    }

    @Override
    public ProjectDto toDto(Project model) {
        if (model == null) {
            return null;
        }
        ProjectDto dto = super.toDto(model);
        List<User> users = new ArrayList<>(model.getUsers());
        users.sort(new User.UserComparator());
        dto.setUsers(userMapper.toDto(users));
        Map<String, String> properties = new HashMap<>();
        for (ProjectProperty property : model.getProjectProperties()) {
            properties.put(property.getPropertyKey(), property.getPropertyValue());
        }
        dto.setProperties(properties);

        return dto;
    }

    @Override
    protected Project constructModel() {
        return new Project();
    }

    @Override
    protected ProjectDto constructDto() {
        return new ProjectDto();
    }

    @Override
    protected String[] getIgnoreProperties() {
        return new String[]{"users", "projectProperties", "properties"};
    }

    public ProjectDto toDtoNoUsers(Project model) {
        if (model == null) {
            return null;
        }
        ProjectDto dto = super.toDto(model);
        dto.setUsers(new ArrayList<>());
        dto.setProperties(new HashMap<>());
        return dto;
    }
}

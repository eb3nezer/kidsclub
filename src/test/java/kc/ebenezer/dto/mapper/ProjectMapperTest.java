package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.ProjectDto;
import kc.ebenezer.dto.UserDto;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.ProjectProperty;
import kc.ebenezer.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMapperTest {
    private static final Long PROJECT_ID = 123415632L;
    private static final String NAME = "My Project";
    private static final Long USER_ID = 32443L;
    private static final String USER_NAME = "Brooke Jones";

    @Mock
    private UserMapper userMapper;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private PersistenceUnitUtil persistenceUnitUtil;

    @InjectMocks
    private ProjectMapper projectMapper;

    @Before
    public void before() {
        when(entityManager.getEntityManagerFactory()).thenReturn(entityManagerFactory);
        when(entityManagerFactory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto(
            USER_ID,
            USER_NAME,
            "Brooke",
            "Jones",
            "brooke@here.com",
            null,
            "12345 657",
            true,
            true,
            null,
            "abcde",
            null,
            null,
            Collections.singletonList("VIEW_STATISTICS"));

        return userDto;
    }

    private User createUser() {
        User user = new User();
        user.setName(USER_NAME);
        user.setId(USER_ID);

        return user;
    }

    @Test
    public void toModel() {
        Map<String, String> properties = new HashMap<>();
        properties.put("key", "value");

        UserDto userDto = createUserDto();

        ProjectDto dto = new ProjectDto(
            PROJECT_ID,
            NAME,
            Collections.singletonList(userDto),
            properties
        );

        Project project = projectMapper.toModel(dto);

        assertEquals(PROJECT_ID, project.getId());
        assertEquals(NAME, project.getName());
        // We don't map users to model ... yet
        assertEquals(0, project.getUsers().size());
        assertEquals(1, project.getProjectProperties().size());
        assertEquals("key", project.getProjectProperties().iterator().next().getPropertyKey());
        assertEquals("value", project.getProjectProperties().iterator().next().getPropertyValue());
    }

    @Test
    public void toDto() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setName(NAME);
        ProjectProperty projectProperty = new ProjectProperty(project, "key", "value");
        project.setProjectProperties(Collections.singleton(projectProperty));

        User user = createUser();
        project.setUsers(Collections.singleton(user));

        UserDto userDto = createUserDto();
        List<UserDto> users = Collections.singletonList(userDto);
        when(userMapper.toDto(isA(List.class))).thenReturn(users);

        when(persistenceUnitUtil.isLoaded(project, "users")).thenReturn(true);

        ProjectDto dto = projectMapper.toDto(project);

        assertEquals(NAME, dto.getName());
        assertEquals(PROJECT_ID, dto.getId());
        assertEquals(1, dto.getProperties().keySet().size());
        assertEquals("value", dto.getProperties().get("key"));
        assertEquals(1, dto.getUsers().size());
        assertEquals(userDto, dto.getUsers().get(0));
    }

    @Test
    public void toDtoUsersNotLoaded() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setName(NAME);
        ProjectProperty projectProperty = new ProjectProperty(project, "key", "value");
        project.setProjectProperties(Collections.singleton(projectProperty));

        User user = createUser();
        project.setUsers(Collections.singleton(user));

        UserDto userDto = createUserDto();
        List<UserDto> users = Collections.singletonList(userDto);
        when(userMapper.toDto(isA(List.class))).thenReturn(users);

        when(persistenceUnitUtil.isLoaded(project, "users")).thenReturn(false);

        ProjectDto dto = projectMapper.toDto(project);

        assertEquals(NAME, dto.getName());
        assertEquals(PROJECT_ID, dto.getId());
        assertEquals(1, dto.getProperties().keySet().size());
        assertEquals("value", dto.getProperties().get("key"));
        assertEquals(0, dto.getUsers().size());
    }

    @Test
    public void toDtoNoUsers() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setName(NAME);
        ProjectProperty projectProperty = new ProjectProperty(project, "key", "value");
        project.setProjectProperties(Collections.singleton(projectProperty));

        User user = createUser();
        project.setUsers(Collections.singleton(user));

        UserDto userDto = createUserDto();
        List<UserDto> users = Collections.singletonList(userDto);
        when(userMapper.toDto(isA(List.class))).thenReturn(users);

        when(persistenceUnitUtil.isLoaded(project, "users")).thenReturn(true);

        ProjectDto dto = projectMapper.toDtoNoUsers(project);

        assertEquals(NAME, dto.getName());
        assertEquals(PROJECT_ID, dto.getId());
        assertEquals(0, dto.getProperties().keySet().size());
        assertEquals(0, dto.getUsers().size());
        verifyZeroInteractions(persistenceUnitUtil);
    }
}

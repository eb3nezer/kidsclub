package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.StudentDto;
import kc.ebenezer.dto.StudentTeamDto;
import kc.ebenezer.model.*;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.service.ProjectPermissionService;
import kc.ebenezer.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StudentMapperTest {
    @Mock
    private StudentTeamMapper studentTeamMapper;
    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;
    @Mock
    private ImageCollectionMapper imageCollectionMapper;
    @Mock
    private ProjectPermissionService projectPermissionService;
    @Mock
    private UserService userService;

    @InjectMocks
    private StudentMapper studentMapper;

    @Test
    public void toModel() {
        long now = System.currentTimeMillis() - 199L;
        StudentTeamDto studentTeam = new StudentTeamDto();
        StudentDto dto = new StudentDto(
                243L,
                "A Student",
                "A",
                "Student",
                "abcde",
                "Mummy Student",
                "Mum",
                "mum@students.com",
                "9876 5432",
                "School of life",
                7,
                "M",
                "allergic to food",
                true,
                "K",
                4552345L,
                studentTeam,
                now,
                now);
        Student model = studentMapper.toModel(dto);
        assertEquals("ID does not match", 243L, model.getId().longValue());
        assertEquals("name does not match", "A Student", model.getName());
        assertEquals("given name does not match", "A", model.getGivenName());
        assertEquals("family name does not match", "Student", model.getFamilyName());
        assertEquals("Contact name does not match", "Mummy Student", model.getContactName());
        assertEquals("contact relationship does not match", "Mum", model.getContactRelationship());
        assertEquals("phone does not match", "9876 5432", model.getPhone());
        assertEquals("Age does not match", 7, model.getAge().intValue());
        assertEquals("special instructions does not match", "allergic to food", model.getSpecialInstructions());
        assertEquals("school year does not match", "K", model.getSchoolYear());
        assertTrue("media permission does not match", model.getMediaPermitted());
    }

    @Test
    public void toDtoUserHasViewPermission() {
        StudentTeam studentTeam = new StudentTeam();
        StudentTeamDto studentTeamDto = new StudentTeamDto();
        when(studentTeamMapper.toDtoShallow(studentTeam)).thenReturn(studentTeamDto);

        Project project = new Project();
        User user = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(projectPermissionService.userHasPermission(user, project, ProjectPermission.VIEW_STUDENTS)).thenReturn(true);
        Student model = new Student(
                243L,
                "A Student",
                "A",
                "Student",
                "abcde",
                "Mummy Student",
                "Mum",
                "mum@students.com",
                "9876 5432",
                "School of life",
                7,
                "K",
                Gender.OTHER,
                "allergic to food",
                true,
                project,
                studentTeam,
            null);
        StudentDto dto = studentMapper.toDto(model);
        assertEquals("ID does not match", 243L, model.getId().longValue());
        assertEquals("name does not match", "A Student", dto.getName());
        assertEquals("given name does not match", "A", dto.getGivenName());
        assertEquals("family name does not match", "Student", dto.getFamilyName());
        assertEquals("Contact name does not match", "Mummy Student", dto.getContactName());
        assertEquals("contact relationship does not match", "Mum", dto.getContactRelationship());
        assertEquals("phone does not match", "9876 5432", dto.getPhone());
        assertEquals("Age does not match", 7, dto.getAge().intValue());
        assertEquals("special instructions does not match", "allergic to food", dto.getSpecialInstructions());
        assertEquals("school year does not match", "K", dto.getSchoolYear());
        assertEquals("gender does not match", "Other", dto.getGender());
        assertEquals("wrong team returned", studentTeamDto, dto.getStudentTeam());
        assertTrue("media permitted incorrect", dto.getMediaPermitted());
    }

    @Test
    public void toDtoUserDoesNotHaveViewPermission() {
        long now = System.currentTimeMillis() - 199L;
        StudentTeam studentTeam = new StudentTeam();
        StudentTeamDto studentTeamDto = new StudentTeamDto();
        when(studentTeamMapper.toDtoShallow(studentTeam)).thenReturn(studentTeamDto);

        Project project = new Project();
        User user = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(projectPermissionService.userHasPermission(user, project, ProjectPermission.VIEW_STUDENTS)).thenReturn(false);
        Student model = new Student(
            243L,
            "A Student",
            "A",
            "Student",
            "abcde",
            "Mummy Student",
            "Mum",
            "mum@students.com",
            "9876 5432",
            "School of life",
            7,
            "K",
            Gender.OTHER,
            "allergic to food",
            true,
            project,
            studentTeam,
            null);
        StudentDto dto = studentMapper.toDto(model);
        assertEquals("ID does not match", 243L, model.getId().longValue());
        assertEquals("name does not match", "A Student", dto.getName());
        assertEquals("given name does not match", "A", dto.getGivenName());
        assertEquals("family name does not match", "Student", dto.getFamilyName());
        assertNull("Contact name should be null", dto.getContactName());
        assertNull("contact relationship should be null", dto.getContactRelationship());
        assertNull("phone should be null", dto.getPhone());
        assertNull("Age should be null", dto.getAge());
        assertNull("special instructions should be null", dto.getSpecialInstructions());
        assertNull("school year should be null", dto.getSchoolYear());
        assertNull("gender should be null", dto.getGender());
        assertEquals("wrong team returned", studentTeamDto, dto.getStudentTeam());
        assertTrue("media permitted incorrect", dto.getMediaPermitted());
    }
}

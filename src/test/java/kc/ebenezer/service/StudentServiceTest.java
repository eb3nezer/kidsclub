package kc.ebenezer.service;

import kc.ebenezer.dao.StudentDao;
import kc.ebenezer.model.*;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StudentServiceTest {
    private static final Long STUDENT_ID = 123432L;
    private static final Long PROJECT_ID = 343634L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private StudentDao studentDao;
    @Mock
    private StudentTeamService studentTeamService;
    @Mock
    private AuditService auditService;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserService userService;
    @Mock
    private MediaService mediaService;
    @Mock
    private ProjectPermissionService projectPermissionService;
    @Mock
    private StudentCSVImporterExporter studentCSVImporterExporter;
    @Mock
    private ImageScalingService imageScalingService;

    @Mock
    private Project project;
    @Mock
    private StudentTeam studentTeam;

    private StudentService studentService;

    @Before
    public void setUp() {
        studentService = new StudentService(
            studentDao,
            studentTeamService,
            auditService,
            projectService,
            userService,
            mediaService,
            projectPermissionService,
            studentCSVImporterExporter,
            imageScalingService);
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    private Student createTestStudent() {
        Student model = new Student(
            STUDENT_ID,
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

        return model;
    }

    @Test
    public void getStudentByIdExists() {
        Student student = createTestStudent();
        when(studentDao.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        Optional<Student> result = studentService.getStudentById(STUDENT_ID);
        assertTrue("Student should exist", result.isPresent());
        assertEquals("Wrong student returned", student, result.get());
    }

    @Test
    public void getStudentByIdDoesNotExist() {
        when(studentDao.findById(STUDENT_ID)).thenReturn(Optional.empty());
        Optional<Student> result = studentService.getStudentById(STUDENT_ID);
        assertFalse("Student should not exist", result.isPresent());
    }

    private void setupAnonymousUser() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
    }

    private void setupUserNoEditStudentsPermission() {
        User user = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(projectService.getProjectById(user, PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectPermissionService.userHasPermission(user, project, ProjectPermission.EDIT_STUDENTS)).thenReturn(false);
    }

    private void setupUserHasEditStudentsPermission() {
        User user = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(projectService.getProjectById(user, PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectPermissionService.userHasPermission(user, project, ProjectPermission.EDIT_STUDENTS)).thenReturn(true);
    }

    @Test
    public void createStudentNoCurrentUser() {
        setupAnonymousUser();
        Student student = createTestStudent();
        expectedException.expect(NoPermissionException.class);
        studentService.createStudent(student, PROJECT_ID);
    }

    @Test
    public void createStudentUserNoPermission() {
        setupUserNoEditStudentsPermission();

        Student student = createTestStudent();
        expectedException.expect(NoPermissionException.class);
        studentService.createStudent(student, PROJECT_ID);
    }

    @Test
    public void createStudentUserHasPermissionInvalidEmail() {
        setupUserHasEditStudentsPermission();

        Student student = createTestStudent();
        student.setEmail("hello");

        expectedException.expect(ValidationException.class);
        studentService.createStudent(student, PROJECT_ID);
    }

    @Test
    public void createStudentUserHasPermission() {
        setupUserHasEditStudentsPermission();

        Student student = createTestStudent();

        when(studentDao.create(student)).thenReturn(student);

        Student result = studentService.createStudent(student, PROJECT_ID);
        assertEquals(student, result);
    }

    @Test
    public void deleteStudentNoCurrentUser() {
        setupAnonymousUser();
        expectedException.expect(NoPermissionException.class);
        studentService.deleteStudent(STUDENT_ID);
    }

    @Test
    public void deleteStudentStudentDoesNotExist() {
        User user = mock(User.class);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(studentDao.findById(STUDENT_ID)).thenReturn(Optional.empty());
        expectedException.expect(ValidationException.class);
        studentService.deleteStudent(STUDENT_ID);
    }

    @Test
    public void deleteStudentUserDoesNotHavePermission() {
        setupUserNoEditStudentsPermission();
        Student student = createTestStudent();
        when(studentDao.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        expectedException.expect(NoPermissionException.class);
        studentService.deleteStudent(STUDENT_ID);
    }

    @Test
    public void deleteStudentSuccess() {
        setupUserHasEditStudentsPermission();
        Student student = createTestStudent();
        when(studentDao.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        Optional<Student> result = studentService.deleteStudent(STUDENT_ID);
        assertTrue("No student returned", result.isPresent());
        assertEquals("Wrong student returned", student, result.get());
    }
}

package kc.ebenezer.service;

import kc.ebenezer.dao.StudentDao;
import kc.ebenezer.dao.StudentTeamDao;
import kc.ebenezer.model.*;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    private static final Logger LOG = LoggerFactory.getLogger(StudentService.class);

    @Inject
    private StudentDao studentDao;
    @Inject
    private StudentTeamService studentTeamService;
    @Inject
    private AuditService auditService;
    @Inject
    private ProjectService projectService;
    @Inject
    private UserService userService;
    @Inject
    private MediaService mediaService;
    @Inject
    private ProjectPermissionService projectPermissionService;
    @Inject
    private StudentCSVImporterExporter studentCSVImporterExporter;

    public StudentService() {
    }

    public Optional<Student> getStudentById(Long id) {
        return studentDao.findById(id);
    }

    public Student createStudent(Student student, Long projectId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create students");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_STUDENTS)) {
            throw new NoPermissionException("You do not have permission to create/edit students");
        }

        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            EmailValidator emailValidator = EmailValidator.getInstance();
            if (!emailValidator.isValid(student.getEmail())) {
                throw new ValidationException("Email is invalid");
            }
        }

        student.setProject(project.get());

        if (student.getMediaPermitted() == null) {
            if (projectService.hasPropertyValue(project.get(), ProjectProperty.STUDENT_MEDIA_PERMITTED_DEFAULT)) {
                student.setMediaPermitted(projectService.getPropertyValueAsBoolean(project.get(), ProjectProperty.STUDENT_MEDIA_PERMITTED_DEFAULT));
            } else {
                student.setMediaPermitted(false);
            }
        }

        try {
            Student created = studentDao.create(student);
            auditService.audit(project.get(), "Created new student, id=" + student.getId() + " name=" + student.getName(),
                    new Date());
            return created;
        } catch (Exception e) {
            LOG.error("Failed to create student", e);
            throw e;
        }
    }

    public Optional<Student> deleteStudent(Long studentId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot delete students");
        }
        Optional<Student> student = studentDao.findById(studentId);
        if (!student.isPresent()) {
            throw new ValidationException("Invalid student");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), student.get().getProject().getId());
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_STUDENTS)) {
            throw new NoPermissionException("You do not have permission to create/edit students");
        }

        if (student.get().getStudentTeam() != null) {
            student.get().getStudentTeam().getStudents().remove(student.get());
        }
        studentDao.delete(student.get());

        auditService.audit(project.get(), "Deleted student, id=" + student.get().getId(), new Date());

        return student;
    }

    public List<Student> bulkCreateStudents(Long projectId, InputStream inputStream) throws IOException {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create students");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_STUDENTS)) {
            throw new NoPermissionException("You do not have permission to create/edit students");
        }

        return studentCSVImporterExporter.bulkCreateStudents(project.get(), inputStream);
    }

    public String exportStudents(Long projectId) throws IOException {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot export students");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.VIEW_STUDENTS)) {
            throw new NoPermissionException("You do not have permission to view students");
        }

        List<Student> students = studentDao.studentsForProject(projectId);

        return studentCSVImporterExporter.bulkExportStudents(students);
    }

    public List<Student> getStudentsForProject(long projectId, Optional<String> nameMatch) {
        Optional<User> currentUser = userService.getCurrentUser();

        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot request students");
        }

        // Check if this user is on this project
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent() && !(SitePermissionService.userHasPermission(currentUser.get(), SitePermission.SYSTEM_ADMIN)
                && !SitePermissionService.userHasPermission(currentUser.get(), SitePermission.LIST_USERS))) {
            throw new NoPermissionException("You do not have permission to list students for this project");
        }

        try {
            if (nameMatch.isPresent()) {
                return studentDao.findForProjectMatchingName(projectId, nameMatch.get(), 10);
            } else {
                return studentDao.studentsForProject(projectId);
            }
        } catch (Exception e) {
            LOG.error("Failed to get students", e);
            throw e;
        }
    }

    private boolean valueUpdated(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return false;
        }

        if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
            return true;
        }

        return !o1.equals(o2);
    }

    public Optional<Student> updateStudent(Long studentId, Long teamId, Student valuesToUpdate) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot update users");
        }

        Optional<Student> existingStudent = getStudentById(studentId);
        if (!existingStudent.isPresent()) {
            throw new ValidationException("Student does not exist");
        }

        if (!ProjectPermissionService.userIsProjectMember(currentUser.get(), existingStudent.get().getProject())) {
            throw new NoPermissionException("You cannot update students on a project you are not a member of");
        }

        Date now = new Date();

        if (!existingStudent.get().getName().equals(valuesToUpdate.getName())) {
            if (valuesToUpdate.getName() == null || valuesToUpdate.getName().isEmpty()) {
                throw new ValidationException("Name cannot be empty");
            }
            existingStudent.get().setName(valuesToUpdate.getName());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set name to \"" + valuesToUpdate.getName() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getEmail(), valuesToUpdate.getEmail())) {
            if (valuesToUpdate.getEmail() != null && !valuesToUpdate.getEmail().isEmpty()) {
                EmailValidator emailValidator = EmailValidator.getInstance();
                if (!emailValidator.isValid(valuesToUpdate.getEmail())) {
                    throw new ValidationException("Email is invalid");
                }
            }

            existingStudent.get().setEmail(valuesToUpdate.getEmail());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set email to \"" + valuesToUpdate.getEmail() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getGivenName(), valuesToUpdate.getGivenName())) {
            existingStudent.get().setGivenName(valuesToUpdate.getGivenName());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set given name to \"" + valuesToUpdate.getGivenName() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getFamilyName(), valuesToUpdate.getFamilyName())) {
            existingStudent.get().setFamilyName(valuesToUpdate.getFamilyName());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set family name to \"" + valuesToUpdate.getFamilyName() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getGender(), valuesToUpdate.getGender())) {
            existingStudent.get().setGender(valuesToUpdate.getGender());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set gender to \"" + valuesToUpdate.getGender().getDescription() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getContactName(), valuesToUpdate.getContactName())) {
            existingStudent.get().setContactName(valuesToUpdate.getContactName());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set emergency contact to \"" + valuesToUpdate.getContactName() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getContactRelationship(), valuesToUpdate.getContactRelationship())) {
            existingStudent.get().setContactRelationship(valuesToUpdate.getContactRelationship());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set contact relationship to \"" + valuesToUpdate.getContactRelationship() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getPhone(), valuesToUpdate.getPhone())) {
            existingStudent.get().setPhone(valuesToUpdate.getPhone());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set phone number to \"" + valuesToUpdate.getPhone() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getAge(), valuesToUpdate.getAge())) {
            existingStudent.get().setAge(valuesToUpdate.getAge());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set age to \"" + valuesToUpdate.getAge() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getSchool(), valuesToUpdate.getSchool())) {
            existingStudent.get().setSchool(valuesToUpdate.getSchool());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set school to \"" + valuesToUpdate.getSchool() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getSchoolYear(), valuesToUpdate.getSchoolYear())) {
            existingStudent.get().setSchoolYear(valuesToUpdate.getSchoolYear());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set school year to \"" + valuesToUpdate.getSchoolYear() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getSpecialInstructions(), valuesToUpdate.getSpecialInstructions())) {
            existingStudent.get().setSpecialInstructions(valuesToUpdate.getSpecialInstructions());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set special instructions to \"" + valuesToUpdate.getSpecialInstructions() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getMediaPermitted(), valuesToUpdate.getMediaPermitted())) {
            existingStudent.get().setMediaPermitted(valuesToUpdate.getMediaPermitted());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set media permitted to \"" + valuesToUpdate.getMediaPermitted() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getMediaDescriptor(), valuesToUpdate.getMediaDescriptor())) {
            if (existingStudent.get().getMediaDescriptor() != null && !existingStudent.get().getMediaDescriptor().isEmpty()) {
                mediaService.deleteData(existingStudent.get().getMediaDescriptor());
                auditService.audit(existingStudent.get().getProject(), "Deleting media \"" + existingStudent.get().getMediaDescriptor() +
                                "\" so it can be replaced with for user id=" + valuesToUpdate.getMediaDescriptor(),
                        now);
            }
            existingStudent.get().setMediaDescriptor(valuesToUpdate.getMediaDescriptor());
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set media descriptor to \"" + valuesToUpdate.getMediaDescriptor() +
                            "\" for student id=" + studentId,
                    now);
        }

        Long existingTeamId = null;
        if (existingStudent.get().getStudentTeam() != null) {
            existingTeamId = existingStudent.get().getStudentTeam().getId();
        }
        if (valueUpdated(existingTeamId, teamId)) {
            if (existingStudent.get().getStudentTeam() != null) {
                existingStudent.get().getStudentTeam().getStudents().remove(existingStudent.get());
                existingStudent.get().setStudentTeam(null);
            }
            if (teamId != null) {
                Optional<StudentTeam> newTeam = studentTeamService.getStudentTeam(teamId);
                if (!newTeam.isPresent()) {
                    throw new ValidationException("Invalid team ID.");
                }
                existingStudent.get().setStudentTeam(newTeam.get());
                newTeam.get().getStudents().add(existingStudent.get());
            }
            existingStudent.get().setUpdated(now);
            auditService.audit(existingStudent.get().getProject(), "Set team to \"" + teamId + "\" for student id=" + studentId,
                    now);
        }

        return existingStudent;
    }
}

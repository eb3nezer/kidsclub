package ebenezer.service;

import ebenezer.dao.StudentDao;
import ebenezer.dao.StudentTeamDao;
import ebenezer.model.*;
import ebenezer.permissions.ProjectPermission;
import ebenezer.permissions.SitePermission;
import ebenezer.rest.NoPermissionException;
import ebenezer.rest.ValidationException;
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
    private StudentTeamDao studentTeamDao;
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

        try {
            Student created = studentDao.create(student);
            auditService.audit("Created new student, id=" + student.getId() + " name=" + student.getName(),
                    new Date());
            return created;
        } catch (Exception e) {
            LOG.error("Failed to create student", e);
            throw e;
        }
    }

    public void deleteStudent(Long studentId, Long projectId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot delete students");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_STUDENTS)) {
            throw new NoPermissionException("You do not have permission to create/edit students");
        }

        Optional<Student> student = studentDao.findById(studentId);
        if (!student.isPresent()) {
            throw new ValidationException("Invalid student");
        }

        if (student.get().getStudentTeam() != null) {
            student.get().getStudentTeam().getStudents().remove(student.get());
        }
        studentDao.delete(student.get());

        auditService.audit("Deleted student, id=" + student.get().getId(), new Date());
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

    public List<StudentTeam> getStudentTeams(Long projectId, Boolean myTeams) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot view teams");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("No project for id=" + projectId);
        }

        List<StudentTeam> teams = studentTeamDao.getStudentTeamsForProject(projectId);
        List<StudentTeam> result;
        if (!myTeams) {
            result = teams;
        } else {
            result = new ArrayList<>();
            for (StudentTeam studentTeam : teams) {
                List<Long> leaderIDs = studentTeam.getLeaders().stream().map(User::getId).collect(Collectors.toList());
                if (leaderIDs.contains(currentUser.get().getId())) {
                    result.add(studentTeam);
                }
            }
        }

        return result;
    }

    public Optional<StudentTeam> getStudentTeam(Long teamId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot assign students to projects");
        }
        Optional<StudentTeam> team = studentTeamDao.findById(teamId);
        if (team.isPresent()) {
            // Check permission
            Optional<Project> tempProject = projectService.getProjectById(currentUser.get(), team.get().getProject().getId());
            if (!tempProject.isPresent()) {
                throw new NoPermissionException("You do not have permission to view this team");
            }
        }

        return team;
    }

    public Optional<StudentTeam> adjustPoints(Long teamId, Integer adjustment) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot assign students to projects");
        }
        Optional<StudentTeam> team = studentTeamDao.findById(teamId);
        if (!team.isPresent()) {
            throw new ValidationException("Team not found");
        }
        if (!ProjectPermissionService.userIsProjectMember(currentUser.get(), team.get().getProject())) {
            throw new NoPermissionException("You cannot adjust points for teams in other projects");
        }
        team.get().setScore(team.get().getScore() + adjustment);
        auditService.audit("Adjusted points by " + adjustment + " for team " + teamId, new Date());
        return team;
    }

    public Optional<StudentTeam> createStudentTeam(Long projectId, StudentTeam studentTeam) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create student teams");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.CREATE_TEAM)) {
            throw new NoPermissionException("You do not have permission to create teams");
        }

        studentTeam.setProject(project.get());
        studentTeam.setScore(0);

        // Find ordering
        List<StudentTeam> existingTeams = getStudentTeams(projectId, false);
        Optional<Integer> max = existingTeams.stream().map(StudentTeam::getSortOrder).max(Integer::compareTo);
        if (max.isPresent()) {
            studentTeam.setSortOrder(max.get() + 1);
        } else {
            studentTeam.setSortOrder(0);
        }

        StudentTeam studentTeam1 = studentTeamDao.create(studentTeam);

        auditService.audit("Created new student team, id=" + studentTeam.getId() + " name=" + studentTeam.getName(),
                new Date());

        return Optional.of(studentTeam);
    }

    public Optional<StudentTeam> updateStudentTeam(Long teamId, StudentTeam valuesToUpdate) {
            Optional<User> currentUser = userService.getCurrentUser();
            if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create student teams");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), valuesToUpdate.getProject().getId());
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.CREATE_TEAM)) {
            throw new NoPermissionException("You do not have permission to update teams");
        }

        Optional<StudentTeam> existingTeam = studentTeamDao.findById(teamId);
        if (!existingTeam.isPresent()) {
            throw new ValidationException("Invalid team");
        }

        Date now = new Date();

        if (valueUpdated(existingTeam.get().getName(), valuesToUpdate.getName())) {
            if (valuesToUpdate.getName() == null || valuesToUpdate.getName().isEmpty()) {
                throw new ValidationException("Name cannot be empty");
            }
            existingTeam.get().setName(valuesToUpdate.getName());
            existingTeam.get().updated();
            auditService.audit("Set name to \"" + valuesToUpdate.getName() + "\" for team id=" + existingTeam.get().getId(),
                    now);
        }

        if (valueUpdated(existingTeam.get().getMediaDescriptor(), valuesToUpdate.getMediaDescriptor())) {
            if (existingTeam.get().getMediaDescriptor() != null && !existingTeam.get().getMediaDescriptor().isEmpty()) {
                mediaService.deleteData(existingTeam.get().getMediaDescriptor());
                auditService.audit("Deleting media \"" + existingTeam.get().getMediaDescriptor() +
                                "\" so it can be replaced with \"" + valuesToUpdate.getMediaDescriptor() + "\" for team id="
                                + existingTeam.get().getId(),
                        now);
            }
            existingTeam.get().setMediaDescriptor(valuesToUpdate.getMediaDescriptor());
            existingTeam.get().updated();
            auditService.audit("Set media descriptor to \"" + valuesToUpdate.getMediaDescriptor() +
                            "\" for team id=" + existingTeam.get().getId(),
                    now);
        }

        // Check if leaders were removed
        Set<User> existingLeaders = new HashSet<>(existingTeam.get().getLeaders());
        for (User existingLeader : existingLeaders) {
            if (!valuesToUpdate.getLeaders().contains(existingLeader)) {
                existingTeam.get().getLeaders().remove(existingLeader);
                existingTeam.get().updated();
                auditService.audit("Remove user id=" + existingLeader.getId() + " as leader from team id=" + existingTeam.get().getId(),
                        now);
            }
        }
        // Check if leaders were added
        Set<User> newLeaders = new HashSet<>(valuesToUpdate.getLeaders());
        for (User newLeader : newLeaders) {
            if (!existingTeam.get().getLeaders().contains(newLeader)) {
                Optional<User> actualLeader = userService.getUserById(newLeader.getId());
                if (actualLeader.isPresent()) {
                    existingTeam.get().getLeaders().add(actualLeader.get());
                    existingTeam.get().updated();
                    auditService.audit("Added user id=" + newLeader.getId() + " as leader to team id=" + existingTeam.get().getId(),
                            now);
                }
            }
        }
        // Check if students were removed
        Set<Student> existingStudents = new HashSet<>(existingTeam.get().getStudents());
        for (Student existingStudent : existingStudents) {
            if (!valuesToUpdate.getStudents().contains(existingStudent)) {
                existingTeam.get().getStudents().remove(existingStudent);
                existingTeam.get().updated();
                auditService.audit("Remove student id=" + existingStudent.getId() + " from team id=" + existingTeam.get().getId(),
                        now);
            }
        }
        // Check if leaders were added
        Set<Student> newStudents = new HashSet<>(valuesToUpdate.getStudents());
        for (Student newStudent : newStudents) {
            if (!existingTeam.get().getStudents().contains(newStudent)) {
                Optional<Student> actualStudent = studentDao.findById(newStudent.getId());
                if (actualStudent.isPresent()) {
                    existingTeam.get().getStudents().add(actualStudent.get());
                    existingTeam.get().updated();
                    auditService.audit("Added student id=" + newStudent.getId() + " to team id=" + existingTeam.get().getId(),
                            now);
                }
            }
        }

        return existingTeam;
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
            auditService.audit("Set name to \"" + valuesToUpdate.getName() + "\" for student id=" + studentId,
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
            auditService.audit("Set email to \"" + valuesToUpdate.getEmail() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getGivenName(), valuesToUpdate.getGivenName())) {
            existingStudent.get().setGivenName(valuesToUpdate.getGivenName());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set given name to \"" + valuesToUpdate.getGivenName() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getFamilyName(), valuesToUpdate.getFamilyName())) {
            existingStudent.get().setFamilyName(valuesToUpdate.getFamilyName());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set family name to \"" + valuesToUpdate.getFamilyName() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getGender(), valuesToUpdate.getGender())) {
            existingStudent.get().setGender(valuesToUpdate.getGender());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set gender to \"" + valuesToUpdate.getGender().getDescription() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getPhone(), valuesToUpdate.getPhone())) {
            existingStudent.get().setPhone(valuesToUpdate.getPhone());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set phone number to \"" + valuesToUpdate.getPhone() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getAge(), valuesToUpdate.getAge())) {
            existingStudent.get().setAge(valuesToUpdate.getAge());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set age to \"" + valuesToUpdate.getAge() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getSchool(), valuesToUpdate.getSchool())) {
            existingStudent.get().setSchool(valuesToUpdate.getSchool());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set school to \"" + valuesToUpdate.getSchool() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getSchoolYear(), valuesToUpdate.getSchoolYear())) {
            existingStudent.get().setSchoolYear(valuesToUpdate.getSchoolYear());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set school year to \"" + valuesToUpdate.getSchoolYear() + "\" for student id=" + studentId,
                    now);
        }

        if (valueUpdated(existingStudent.get().getMediaDescriptor(), valuesToUpdate.getMediaDescriptor())) {
            if (existingStudent.get().getMediaDescriptor() != null && !existingStudent.get().getMediaDescriptor().isEmpty()) {
                mediaService.deleteData(existingStudent.get().getMediaDescriptor());
                auditService.audit("Deleting media \"" + existingStudent.get().getMediaDescriptor() +
                                "\" so it can be replaced with for user id=" + valuesToUpdate.getMediaDescriptor(),
                        now);
            }
            existingStudent.get().setMediaDescriptor(valuesToUpdate.getMediaDescriptor());
            existingStudent.get().setUpdated(now);
            auditService.audit("Set media descriptor to \"" + valuesToUpdate.getMediaDescriptor() +
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
                Optional<StudentTeam> newTeam = getStudentTeam(teamId);
                if (!newTeam.isPresent()) {
                    throw new ValidationException("Invalid team ID.");
                }
                existingStudent.get().setStudentTeam(newTeam.get());
                newTeam.get().getStudents().add(existingStudent.get());
            }
            existingStudent.get().setUpdated(now);
            auditService.audit("Set team to \"" + teamId + "\" for student id=" + studentId,
                    now);
        }


        return existingStudent;
    }
}

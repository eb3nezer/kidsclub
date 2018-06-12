package kc.ebenezer.service;

import kc.ebenezer.dao.StudentDao;
import kc.ebenezer.dao.StudentTeamDao;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.Student;
import kc.ebenezer.model.StudentTeam;
import kc.ebenezer.model.User;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class StudentTeamService {
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

        auditService.audit(project.get(), "Created new student team, id=" + studentTeam.getId() + " name=" + studentTeam.getName(),
                new Date());

        return Optional.of(studentTeam);
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
            auditService.audit(project.get(), "Set name to \"" + valuesToUpdate.getName() + "\" for team id=" + existingTeam.get().getId(),
                    now);
        }

        if (valueUpdated(existingTeam.get().getMediaDescriptor(), valuesToUpdate.getMediaDescriptor())) {
            if (existingTeam.get().getMediaDescriptor() != null && !existingTeam.get().getMediaDescriptor().isEmpty()) {
                mediaService.deleteData(existingTeam.get().getMediaDescriptor());
                auditService.audit(project.get(), "Deleting media \"" + existingTeam.get().getMediaDescriptor() +
                                "\" so it can be replaced with \"" + valuesToUpdate.getMediaDescriptor() + "\" for team id="
                                + existingTeam.get().getId(),
                        now);
            }
            existingTeam.get().setMediaDescriptor(valuesToUpdate.getMediaDescriptor());
            existingTeam.get().updated();
            auditService.audit(project.get(), "Set media descriptor to \"" + valuesToUpdate.getMediaDescriptor() +
                            "\" for team id=" + existingTeam.get().getId(),
                    now);
        }

        // Check if leaders were removed
        Set<User> existingLeaders = new HashSet<>(existingTeam.get().getLeaders());
        for (User existingLeader : existingLeaders) {
            if (!valuesToUpdate.getLeaders().contains(existingLeader)) {
                existingTeam.get().getLeaders().remove(existingLeader);
                existingTeam.get().updated();
                auditService.audit(project.get(), "Remove user id=" + existingLeader.getId() + " as leader from team id=" + existingTeam.get().getId(),
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
                    auditService.audit(project.get(), "Added user id=" + newLeader.getId() + " as leader to team id=" + existingTeam.get().getId(),
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
                auditService.audit(project.get(), "Remove student id=" + existingStudent.getId() + " from team id=" + existingTeam.get().getId(),
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
                    auditService.audit(project.get(), "Added student id=" + newStudent.getId() + " to team id=" + existingTeam.get().getId(),
                            now);
                }
            }
        }

        return existingTeam;
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
        auditService.audit(team.get().getProject(), "Adjusted points by " + adjustment + " for team " + teamId, new Date());
        return team;
    }


}

package kc.ebenezer.service;

import kc.ebenezer.dao.AttendanceRecordDao;
import kc.ebenezer.model.*;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

@Component
public class AttendanceService {
    @Inject
    private UserService userService;
    @Inject
    private ProjectService projectService;
    @Inject
    private StudentService studentService;
    @Inject
    private AttendanceRecordDao attendanceRecordDao;
    @Inject
    private ProjectPermissionService projectPermissionService;
    @Inject
    private AuditService auditService;

    private void validateForGet(Optional<User> currentUser, Optional<Project> project) {
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous may not request attendance");
        }

        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }
    }

    private void validateForGetStudent(Optional<User> currentUser, Optional<Project> project, Optional<Student> student) {
        validateForGet(currentUser, project);

        if (!student.isPresent()) {
            throw new ValidationException("Invalid student");
        }

        // Check that this student is a member of this project
        if (!studentService.getStudentsForProject(project.get().getId(), Optional.empty()).contains(student.get())) {
            throw new ValidationException("The specified student is not a member of the specified project");
        }
    }

    private void validateForUpdate(Optional<User> currentUser, Optional<Project> project, Optional<Student> student) {
        validateForGetStudent(currentUser, project, student);

        if (!(projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_ATTENDANCE) ||
            projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.VIEW_STUDENTS))) {
            throw new NoPermissionException("You do not have permission to edit attendance");
        }
    }

    private Date getStartOfDay() {
        GregorianCalendar cal = new GregorianCalendar();
        // Today's date, but midnight
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay() {
        GregorianCalendar cal = new GregorianCalendar();
        // Today's date, but just before midnight
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public List<AttendanceRecord> getTodaysAttendanceForStudent(Long projectId, Long studentId, Optional<Integer> limit) {
        Optional<User> currentUser = userService.getCurrentUser();
        Optional<Project> project = Optional.empty();
        if (currentUser.isPresent()) {
            project = projectService.getProjectById(currentUser.get(), projectId);
        }
        Optional<Student> student = studentService.getStudentById(studentId);
        validateForGetStudent(currentUser, project, student);

        Date startDate = getStartOfDay();
        Date endDate = getEndOfDay();
        List<AttendanceRecord> records = attendanceRecordDao.getAttendanceForStudentAndDateRange(studentId, startDate, endDate, limit);
        if (records.isEmpty()) {
            AttendanceRecord record = new AttendanceRecord(student.get(), AttendanceType.NO_RECORD, startDate, currentUser.get(), "No Data");
            records.add(record);
        }
        return records;
    }

    public List<AttendanceRecord> getTodaysAttendanceForProject(Long projectId, Integer limit) {
        Optional<User> currentUser = userService.getCurrentUser();
        Optional<Project> project = Optional.empty();
        if (currentUser.isPresent()) {
            project = projectService.getProjectById(currentUser.get(), projectId);
        }
        validateForGet(currentUser, project);

        Date startDate = getStartOfDay();
        Date endDate = getEndOfDay();

        return attendanceRecordDao.getAttendanceForDateRange(projectId, startDate, endDate, limit);
    }


    public List<AttendanceRecord> getAllAttendanceForStudent(Long projectId, Long studentId) {
        Optional<User> currentUser = userService.getCurrentUser();
        Optional<Project> project = Optional.empty();
        if (currentUser.isPresent()) {
            project = projectService.getProjectById(currentUser.get(), projectId);
        }
        Optional<Student> student = studentService.getStudentById(studentId);
        validateForGetStudent(currentUser, project, student);

        return attendanceRecordDao.getAttendanceForStudent(studentId);
    }

    public Optional<AttendanceRecord> addAttendance(Long projectId, Long studentId, AttendanceRecord attendanceRecord) {
        Optional<User> currentUser = userService.getCurrentUser();
        Optional<Project> project = Optional.empty();
        if (currentUser.isPresent()) {
            project = projectService.getProjectById(currentUser.get(), projectId);
        }
        Optional<Student> student = studentService.getStudentById(studentId);
        validateForUpdate(currentUser, project, student);

        Date now = new Date();
        attendanceRecord.setVerifier(currentUser.get());
        attendanceRecord.setStudent(student.get());
        attendanceRecord.setRecordTime(now);

        AttendanceRecord result = attendanceRecordDao.create(attendanceRecord);
        auditService.audit("Added attendance for student id=" + studentId + ", project id=" + projectId +
                ", type=" + attendanceRecord.getAttendanceType().getName(), now);

        return Optional.of(result);
    }
}

package ebenezer.rest;

import ebenezer.dto.AttendanceRecordDto;
import ebenezer.dto.mapper.AttendanceRecordMapper;
import ebenezer.model.AttendanceRecord;
import ebenezer.service.AttendanceService;
import ebenezer.service.StatsService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Path("/attendance")
@Transactional
public class AttendanceResource {
    @Inject
    private AttendanceRecordMapper attendanceRecordMapper;
    @Inject
    private AttendanceService attendanceService;
    @Inject
    private StatsService statsService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/project/{projectId}/student/{studentId}")
    public Response addAttendance(
            @PathParam("projectId") Long projectId,
            @PathParam("studentId") Long studentId,
            AttendanceRecordDto attendanceRecord)  {
        Optional<AttendanceRecord> result = attendanceService.addAttendance(projectId, studentId, attendanceRecordMapper.toModel(attendanceRecord));
        if (result.isPresent()) {
            logStats("rest.attendance.add", projectId, studentId);
            return Response.status(Response.Status.OK).entity(attendanceRecordMapper.toDto(result.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/project/{projectId}")
    public Response getTodaysAttendanceForAll(
            @PathParam("projectId") Long projectId,
            @QueryParam("limit") Integer limit) {

        if (limit == null) {
            limit = 5;
        }
        List<AttendanceRecord> attendanceRecords = attendanceService.getTodaysAttendanceForProject(projectId, limit);
        logStats("rest.attendance.get.project.today", projectId, null);
        return Response.status(Response.Status.OK).entity(attendanceRecordMapper.toDto(attendanceRecords)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/project/{projectId}/student/{studentId}")
    public Response getTodaysAttendanceForStudent(
            @PathParam("projectId") Long projectId,
            @PathParam("studentId") Long studentId,
            @QueryParam("limit") Integer limitParam) {

        Optional<Integer> limit = Optional.ofNullable(limitParam);
        List<AttendanceRecord> attendanceRecords = attendanceService.getTodaysAttendanceForStudent(projectId, studentId, limit);
        logStats("rest.attendance.get.today", projectId, studentId);
        return Response.status(Response.Status.OK).entity(attendanceRecordMapper.toDto(attendanceRecords)).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/project/{projectId}/student/{studentId}/all")
    public Response getAllAttendanceForStudent(
            @PathParam("projectId") Long projectId,
            @PathParam("studentId") Long studentId) {

        List<AttendanceRecord> attendanceRecords = attendanceService.getAllAttendanceForStudent(projectId, studentId);
        logStats("rest.attendance.get", projectId, studentId);
        return Response.status(Response.Status.OK).entity(attendanceRecordMapper.toDto(attendanceRecords)).build();
    }

    private void logStats(String key, Long projectId, Long studentId) {
        Map<String, String> metadata = new HashMap<>();
        if (projectId != null) {
            metadata.put("PROJECT", projectId.toString());
            if (studentId != null) {
                metadata.put("STUDENT", studentId.toString());
            }
        }
        statsService.logStats(key, metadata);
    }
}

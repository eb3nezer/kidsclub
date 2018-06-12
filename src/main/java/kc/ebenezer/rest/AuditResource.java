package kc.ebenezer.rest;

import kc.ebenezer.dto.ProjectDto;
import kc.ebenezer.dto.mapper.AuditRecordMapper;
import kc.ebenezer.dto.mapper.ProjectMapper;
import kc.ebenezer.model.AuditRecord;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.User;
import kc.ebenezer.service.AuditService;
import kc.ebenezer.service.ProjectService;
import kc.ebenezer.service.StatsService;
import kc.ebenezer.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Path("/audit")
public class AuditResource {
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    @Inject
    private AuditService auditService;
    @Inject
    private AuditRecordMapper auditRecordMapper;
    @Inject
    private StatsService statsService;

    @GET
    @Produces("application/json")
    public Response getAuditRecords(
        @QueryParam("startDate") String startDateText,
        @QueryParam("endDate") String endDateText,
        @QueryParam("projectId") Long projectId,
        @QueryParam("count") Integer count,
        @QueryParam("start") Integer start) {
        if (count == null) {
            count = 100;
        }
        if (start == null) {
            start = 0;
        }
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date startDate;
        try {
            startDate = dateFormat.parse("1900/01/01 00:00:00");
            if (startDateText != null) {
                startDate = dateFormat.parse(startDateText);
            }
            Date endDate = new Date();
            if (endDateText != null) {
                endDate = dateFormat.parse(endDateText);
            }
            List<AuditRecord> records = auditService.getAuditRecords(startDate, endDate, Optional.ofNullable(projectId),0, 100);
            statsService.logStats("rest.media.download", new HashMap<>());

            return Response.status(Response.Status.OK).entity(auditRecordMapper.toDto(records)).build();
        } catch (ParseException e) {
            throw new ValidationException("Could not validate", e);
        } catch (Throwable t) {
            throw new ValidationException("Unknown error", t);
        }
    }
}

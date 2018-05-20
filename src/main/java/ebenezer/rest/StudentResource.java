package ebenezer.rest;

import ebenezer.Application;
import ebenezer.dto.ProjectDto;
import ebenezer.dto.StudentDto;
import ebenezer.dto.StudentTeamDto;
import ebenezer.dto.UserDto;
import ebenezer.dto.mapper.StudentMapper;
import ebenezer.dto.mapper.StudentTeamMapper;
import ebenezer.model.Media;
import ebenezer.model.Student;
import ebenezer.model.StudentTeam;
import ebenezer.model.User;
import ebenezer.service.MediaService;
import ebenezer.service.StatsService;
import ebenezer.service.StudentService;
import ebenezer.service.UserService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.codehaus.jackson.map.BeanProperty;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Path("/students")
public class StudentResource {
    @Inject
    private StudentService studentService;
    @Inject
    private StudentMapper studentMapper;
    @Inject
    private StudentTeamMapper studentTeamMapper;
    @Inject
    private UserService userService;
    @Inject
    private MediaService mediaService;
    @Inject
    private StatsService statsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listStudents(@QueryParam("projectId") String project, @QueryParam("name") String name) {
        List<Student> students = studentService.getStudentsForProject(Long.valueOf(project), Optional.ofNullable(name));

        List<StudentDto> studentDtos = studentMapper.toDto(students);
        logStats("rest.students.get", project);
        return Response.status(Response.Status.OK).entity(studentDtos).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") String id) {
        Optional<User> currentUser = userService.getCurrentUser();

        if (currentUser.isPresent()) {
            Optional<Student> student = studentService.getStudentById(Long.valueOf(id));
            if (student.isPresent()) {
                logStats("rest.student.get", student.get().getProject().getId().toString());
                return Response.status(Response.Status.OK).entity(studentMapper.toDto(student.get())).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStudent(StudentDto studentDto) {
        Student createRequest = studentMapper.toModel(studentDto);
        Student created = studentService.createStudent(createRequest, studentDto.getProjectId());
        logStats("rest.student.create", studentDto.getProjectId().toString());

        return Response.status(Response.Status.OK).entity(studentMapper.toDto(created)).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateStudent(
            @Context HttpServletRequest request,
            @FormDataParam("id") String studentId,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail,
            @FormDataParam("name") String name,
            @FormDataParam("givenName") String givenName,
            @FormDataParam("familyName") String familyName,
            @FormDataParam("phone") String phone,
            @FormDataParam("email") String email,
            @FormDataParam("age") String age,
            @FormDataParam("school") String school,
            @FormDataParam("schoolYear") String schoolYear,
            @FormDataParam("mediaDescriptor") String mediaDescriptor,
            @FormDataParam("team") String team
    ) throws IOException {
        if (mediaDescriptor != null && mediaDescriptor.isEmpty()) {
            mediaDescriptor = null;
        }

        // Check for file upload
        if (payload != null && payloadDetail != null && !payloadDetail.getFileName().isEmpty()) {
            int fileSize = request.getContentLength();
            if (fileSize > Application.MAX_UPLOAD_SIZE) {
                throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
            }

            String filename = payloadDetail.getFileName().toLowerCase();
            Optional<Media> media = mediaService.storeData(payload, fileSize, filename, true, name);
            if (media.isPresent()) {
                mediaDescriptor = media.get().getDescriptor();
            }
        }

        Integer ageInt = null;
        if (age != null && !age.isEmpty()) {
            ageInt = Integer.valueOf(age);
        }
        Integer schoolYearInt = null;
        if (schoolYear != null && !schoolYear.isEmpty()) {
            schoolYearInt = Integer.valueOf(schoolYear);
        }
        StudentDto studentDto = new StudentDto(
                Long.valueOf(studentId),
                name,
                givenName,
                familyName,
                null,
                mediaDescriptor,
                email,
                phone,
                school,
                ageInt,
                schoolYearInt,
                null,
                null,
                null,
                null
        );

        Student updateRequest = studentMapper.toModel(studentDto);
        Long teamId = null;
        if (team != null && !team.isEmpty()) {
            teamId = Long.valueOf(team);
        }
        Optional<Student> updated = studentService.updateStudent(studentDto.getId(), teamId, updateRequest);
        if (updated.isPresent()) {
            logStats("rest.student.update", updated.get().getProject().getId().toString());
            return Response.status(Response.Status.OK).entity(studentMapper.toDto(updated.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/teams")
    public Response createStudentTeam(
            @Context HttpServletRequest request,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail,
            @FormDataParam("name") String name,
            @FormDataParam("avatarUrl") String avatarUrl,
            @FormDataParam("projectId") String projectId) throws IOException {

        StudentTeamDto studentTeamDto = new StudentTeamDto();

        // Check for file upload
        if (payload != null && payloadDetail != null && !payloadDetail.getFileName().isEmpty()) {
            int fileSize = request.getContentLength();
            if (fileSize > Application.MAX_UPLOAD_SIZE) {
                throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
            }

            String filename = payloadDetail.getFileName().toLowerCase();
            Optional<Media> media = mediaService.storeData(payload, fileSize, filename, true, name);
            if (media.isPresent()) {
                studentTeamDto.setMediaDescriptor(media.get().getDescriptor());
            }
        } else {
            studentTeamDto.setAvatarUrl(avatarUrl);
        }

        studentTeamDto.setName(name);

        Optional<StudentTeam> studentTeam = studentService.createStudentTeam(Long.valueOf(projectId), studentTeamMapper.toModel(studentTeamDto));
        if (studentTeam.isPresent()) {
            logStats("rest.student.team.create", studentTeam.get().getProject().getId().toString());
            return Response.status(Response.Status.OK).entity(studentTeamMapper.toDto(studentTeam.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/teams/{id}")
    public Response updateStudentTeam(
            @Context HttpServletRequest request,
            @PathParam("id") String teamId,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail,
            @FormDataParam("name") String name,
            @FormDataParam("avatarUrl") String avatarUrl,
            @FormDataParam("projectId") String projectId,
            @FormDataParam("mediaDescriptor") String mediaDescriptor,
            @FormDataParam("leaderList") String leaderList,
            @FormDataParam("studentList") String studentList
            ) throws IOException {

        // Check for file upload
        if (payload != null && payloadDetail != null && !payloadDetail.getFileName().isEmpty()) {
            int fileSize = request.getContentLength();
            if (fileSize > Application.MAX_UPLOAD_SIZE) {
                throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
            }

            String filename = payloadDetail.getFileName().toLowerCase();
            Optional<Media> media = mediaService.storeData(payload, fileSize, filename, true, name);
            if (media.isPresent()) {
                mediaDescriptor = media.get().getDescriptor();
            }
        }

        ProjectDto project = new ProjectDto(Long.valueOf(projectId), null, null);
        List<UserDto> leaders = new ArrayList<>();
        if (leaderList != null && !leaderList.isEmpty()) {
            String leaderIDs[] = leaderList.split(",");
            leaders = Arrays.stream(leaderIDs).map(id -> new UserDto(Long.valueOf(id))).collect(Collectors.toList());
        }
        List<StudentDto> students = new ArrayList<>();
        if (studentList != null && !studentList.isEmpty()) {
            String studentIDs[] = studentList.split(",");
            students = Arrays.stream(studentIDs).map(id -> new StudentDto(Long.valueOf(id))).collect(Collectors.toList());
        }

        StudentTeamDto updateRequest = new StudentTeamDto(
                null,
                project,
                name,
                null,
                leaders,
                students,
                avatarUrl,
                mediaDescriptor,
                null,
                null
        );

        Optional<StudentTeam> studentTeam = studentService.updateStudentTeam(Long.valueOf(teamId), studentTeamMapper.toModel(updateRequest));
        if (studentTeam.isPresent()) {
            logStats("rest.student.team.update", projectId);
            return Response.status(Response.Status.OK).entity(studentTeamMapper.toDto(studentTeam.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/teams")
    public Response getStudentTeams(@QueryParam("projectId") Long projectId,
                                    @QueryParam("mine") String mine) {
        if (projectId == null) {
            throw new ValidationException("projectId must be supplied");
        }
        List<StudentTeam> teams = studentService.getStudentTeams(projectId, Boolean.valueOf(mine));
        teams.sort(new StudentTeam.StudentTeamComparator());
        logStats("rest.student.teams.get", projectId.toString());
        return Response.status(Response.Status.OK).entity(studentTeamMapper.toDto(teams)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/teams/{id}")
    public Response getStudentTeam(@PathParam("id") String teamId) {
        Optional<StudentTeam> team = studentService.getStudentTeam(Long.valueOf(teamId));
        if (team.isPresent()) {
            logStats("rest.student.team.get", team.get().getProject().getId().toString());
            return Response.status(Response.Status.OK).entity(studentTeamMapper.toDto(team.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/teams/{id}/points/{adjustment}")
    public Response adjustPointsForTeam(@PathParam("id") String teamId, @PathParam("adjustment") String adjustment) {
        Optional<StudentTeam> team = studentService.adjustPoints(Long.valueOf(teamId), Integer.valueOf(adjustment));

        if (team.isPresent()) {
            logStats("rest.student.team.points", team.get().getProject().getId().toString());
            return Response.status(Response.Status.OK).entity(studentTeamMapper.toDto(team.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private void logStats(String key, String projectId) {
        Map<String, String> metadata = new HashMap<>();
        if (projectId != null) {
            metadata.put("PROJECT", projectId);
        }
        statsService.logStats(key, metadata);
    }
}

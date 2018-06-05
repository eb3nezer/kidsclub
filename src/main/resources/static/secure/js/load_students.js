function loadStudents(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/students/?projectId=${projectId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function loadStudent(studentId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/students/${studentId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function loadAttendanceForStudent(projectId, studentId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/attendance/project/${projectId}/student/${studentId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function loadAttendanceForProject(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/attendance/project/${projectId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function loadStudentMatchingForProject(projectId, query, doneCallback, doneCallbackArgs, failCallback) {
    var url;
    if (projectId) {
        url = `/rest/students/?name=${query}&projectId=${projectId}`;
    } else {
        url = `/rest/students/?name=${query}`;
    }
    $.ajax({
        url: url
    }).done(function(result) {
        doneCallback(result, doneCallbackArgs);
    }).fail(failCallback);
}

function renderStudentForAdminList(student) {
    var name = escapeHtml(student.name);
    var alert = "";
    if (student.specialInstructions) {
        alert = " <img src='/secure/icons/exclamation.gif' alt='Notes' height='25'>";
    }
    var noPhotos = "";
    if (!student.mediaPermitted) {
        noPhotos = " <img src='/secure/icons/camera-off-black.png' alt='No photos' width='25' height='25'>";
    }
    var teamLink = "(<i>No team assigned</i>)";
    if (student.studentTeam && student.studentTeam.name) {
        var teamName = escapeHtml(student.studentTeam.name);
        teamLink = `<a href="edit_student_team.html#project=${projectId}&team=${student.studentTeam.id}">(${teamName})</a>`;
    }
    var html = '<div class="user_selector_container"><div class="user_selector_user">';
    html += `<a href="edit_student.html#project=${projectId}&student=${student.id}">${name}</a>${alert}${noPhotos}</div><div class="user_selector_delete" onclick="deleteStudent(${student.id})">x</div> ${teamLink}</div>`;
    return html;
}

function renderStudentForList(student, includeTeamLink) {
    var name = escapeHtml(student.name);
    var alert = "";
    if (student.specialInstructions) {
        alert = " <img src='/secure/icons/exclamation.gif' alt='Notes' height='25'>";
    }
    var noPhotos = "";
    if (!student.mediaPermitted) {
        noPhotos = " <img src='/secure/icons/camera-off-black.png' alt='No photos' width='25' height='25'>";
    }
    var teamLink = "";
    if (includeTeamLink) {
        teamLink = "(<i>No team assigned</i>)";
        if (student.studentTeam && student.studentTeam.name) {
            var teamName = escapeHtml(student.studentTeam.name);
            teamLink = `(<a href="view_student_team.html#project=${projectId}&team=${student.studentTeam.id}">${teamName}</a>)`;
        }
    }
    var html = `<a href="view_student.html#project=${projectId}&student=${student.id}">${name}</a>${alert}${noPhotos} ${teamLink}`;
    return html;
}

function renderStudentAttendanceForList(projectId, attendance, includeTeamLink) {
    var name = escapeHtml(attendance.student.name);
    var alert = "";
    if (attendance.student.specialInstructions) {
        alert = " <img src='/secure/icons/exclamation.gif' alt='Notes' height='25'>";
    }
    var noPhotos = "";
    if (!attendance.student.mediaPermitted) {
        noPhotos = " <img src='/secure/icons/camera-off-black.png' alt='No photos' width='25' height='25'>";
    }
    var teamLink = "";
    if (includeTeamLink) {
        teamLink = "(<i>No team assigned</i>)";
        if (attendance.student.studentTeam && attendance.student.studentTeam.name) {
            var teamName = escapeHtml(attendance.student.studentTeam.name);
            teamLink = `(<a href="view_student_team.html#project=${projectId}&team=${attendance.student.studentTeam.id}">${teamName}</a>)`;
        }
    }
    var html = `<a href="view_student.html#project=${projectId}&student=${attendance.student.id}">${name}</a>${alert}${noPhotos} ${teamLink}`;
    if (attendance.attendanceCode === 'I') {
        html += "<span style='color: green'>[==]</span>";
    } else if (attendance.attendanceCode === 'O') {
        html += "<span style='color: blue'>[==]</span>";
    } else if (attendance.attendanceCode === 'A') {
        html += "<span style='color: orange'>[==]</span>";
    } else {
        html += "<span style='color: grey'>[==]</span>";
    }
    return html;
}
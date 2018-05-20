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
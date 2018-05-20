function loadProject(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/projects/${projectId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function loadAllProjects(doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/projects/`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

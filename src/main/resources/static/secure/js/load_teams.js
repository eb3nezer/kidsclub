function loadTeams(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/students/teams/?projectId=${projectId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function loadMyTeams(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/students/teams/?projectId=${projectId}&mine=true`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}


function loadTeam(teamId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/students/teams/${teamId}`
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

function adjustPoints(teamId, amount, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/students/teams/${teamId}/points/${amount}`,
        method: 'PUT',
    }).done(function(data) {
        doneFunc(data);
    }).fail(function(xhr, textStatus, error) {
        errorFunc(xhr, textStatus, error);
    });
}

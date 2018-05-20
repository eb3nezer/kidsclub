function loadMyPermissions(projectId, doneFunc, errorFunc) {
    var url;
    if (projectId) {
        url = `/rest/users/me/permissions/?projectId=${projectId}`;
    } else {
        url = "/rest/users/me/permissions/";
    }
    $.ajax({
        url: url
    }).done(doneFunc).fail(errorFunc);
}

function userHasSitePermission(permissions, permission) {
    var result = false;
    if (permissions && permissions.userSitePermissions && permissions.userSitePermissions.length) {
        $.each(permissions.userSitePermissions, function(index, userSitePermission) {
            if (userSitePermission.key === permission && userSitePermission.granted) {
                result = true;
            }
        })
    }
    return result;
}

function userHasProjectPermission(permissions, permission) {
    var result = false;
    if (permissions && permissions.userProjectPermissions && permissions.userProjectPermissions.length) {
        $.each(permissions.userProjectPermissions, function(index, userProjectPermission) {
            if (userProjectPermission.key === permission && userProjectPermission.granted) {
                result = true;
            }
        });
    }
    return result;
}

function loadUserMatchingForProject(projectId, query, doneCallback, doneCallbackArgs, failCallback) {
    var url;
    if (projectId) {
        url = `/rest/users/?name=${query}&projectId=${projectId}`;
    } else {
        url = `/rest/users/?name=${query}`;
    }
    $.ajax({
        url: url
    }).done(function(result) {
        doneCallback(result, doneCallbackArgs);
    }).fail(failCallback);
}

function generateUserTable(users) {
    var html = "<table><tr><th>Name</th><th>E-Mail</th><th>Phone</th><th>Photo</th><th>Notes</th></tr>";
    $.each(users, function(index, user) {
        var userName = escapeHtml(user.name);
        var mobileLink = "";
        if (user.mobilePhone) {
            var mobile = escapeHtml(user.mobilePhone)
            mobileLink = `<a href="tel:${mobile}">${mobile}</a>`;
        }
        var homeLink = ""
        if (user.homePhone) {
            var home = escapeHtml(user.homePhone)
            homeLink = `<a href="tel:${home}">${home}</a>`;
        }
        if (user.mobilePhone && user.homePhone) {
            mobileLink += "<br/>";
        }
        html += `<tr><td>${userName}</td>`;
        html += `<td><a href='mailto:${user.email}'>${user.email}</a></td>`;
        html += `<td>${mobileLink} ${homeLink}</td>`;
        html += '<td>';
        if (user.mediaDescriptor) {
            html = html + `<img src='/rest/data/download/${user.mediaDescriptor}' width='64'/>`;
        } else if (user.avatarUrl) {
            html = html + `<img src='${user.avatarUrl}' width='64'/>`;
        }
        html = html + '</td><td>';
        if (!user.loggedIn) {
            html = html + '(never logged in)';
        }
        html = html + '</td></tr>';
    });
    html = html + "</table>";
    return html;
}

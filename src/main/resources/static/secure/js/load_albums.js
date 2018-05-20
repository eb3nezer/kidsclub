function loadAlbum(albumId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/albums/${albumId}`
    }).done(doneFunc).fail(errorFunc);
}

function loadAlbums(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/albums/?projectId=${projectId}`
    }).done(doneFunc).fail(errorFunc);
}

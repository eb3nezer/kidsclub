function loadDocuments(projectId, doneFunc, errorFunc) {
    $.ajax({
        url: `/rest/documents/?projectId=${projectId}`
    }).done(doneFunc).fail(errorFunc);
}

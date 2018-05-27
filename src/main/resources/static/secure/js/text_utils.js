// Escape potentially dangerous HTML
function escapeHtml(html)
{
    if (!html) {
        return "";
    }
    var text = document.createTextNode(html);
    var div = document.createElement('div');
    div.appendChild(text);
    return div.innerHTML;
}
package ebenezer.permissions;

public enum ProjectPermission {
    CREATE_TEAM("Create a new team"),
    VIEW_STUDENTS("View student details"),
    EDIT_STUDENTS("Create/edit students details"),
    LIST_USERS("View all team members"),
    EDIT_ALBUMS("Create/delete photo albums"),
    PROJECT_ADMIN("Administer the project"),
    EDIT_DOCUMENTS("Create/delete project documents"),
    EDIT_ATTENDANCE("Sign students in or out");

    private String description;

    ProjectPermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

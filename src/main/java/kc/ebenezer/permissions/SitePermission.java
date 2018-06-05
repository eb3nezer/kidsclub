package kc.ebenezer.permissions;

public enum SitePermission {
    INVITE_USERS("Invite users to the site"),
    EDIT_USERS("Edit other users"),
    LIST_USERS("View all users on the site"),
    VIEW_AUDIT("View audit logs"),
    CREATE_PROJECT("Create new projects"),
    VIEW_STATISTICS("View statistical data"),
    SYSTEM_ADMIN("System administrator");

    private String description;

    SitePermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

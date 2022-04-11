package osa.dev.petproject.models;

public enum Permission {
    USER_READ("users:read"),
    USER_WRITE("users:write"),
    USER_CREATE_USERS("users:create_users"),
    OPT_READ("opt:read"),
    OPT_WRITE("opt:write");

    private String permission;

    Permission(String permission){
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}

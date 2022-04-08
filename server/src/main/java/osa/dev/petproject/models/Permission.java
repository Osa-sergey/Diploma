package osa.dev.petproject.models;

public enum Permission {
    USER_READ("users:read"),
    USER_WRITE("users:write"),
    USER_CREATE_USERS("users:create_users");

    private String permission;

    Permission(String permission){
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}

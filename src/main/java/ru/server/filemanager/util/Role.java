package ru.server.filemanager.util;

public enum Role {
    USER("client_user"),
    ADMIN("client_admin");
    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

}

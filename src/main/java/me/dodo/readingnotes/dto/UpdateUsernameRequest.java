package me.dodo.readingnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateUsernameRequest {

    @NotBlank
    private String newUsername;

    public String getNewUsername() { return newUsername; }
    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }
}

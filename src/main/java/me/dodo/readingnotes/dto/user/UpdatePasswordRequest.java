package me.dodo.readingnotes.dto.user;

import jakarta.validation.constraints.NotBlank;

public class UpdatePasswordRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}

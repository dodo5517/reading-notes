package me.dodo.readingnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class ReissueRequest {

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String deviceInfo;

    @Override // toString 예쁘게 보기 위해 오버라이딩
    public String toString() {
        return "Reissue{" +
                "refreshToken='" + refreshToken + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                '}';
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
}

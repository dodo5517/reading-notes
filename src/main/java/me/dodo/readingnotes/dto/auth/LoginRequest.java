package me.dodo.readingnotes.dto.auth;

public class LoginRequest {
    private String email;
    private String password;

    @Override // toString 예쁘게 보기 위해 오버라이딩
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                '}';
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

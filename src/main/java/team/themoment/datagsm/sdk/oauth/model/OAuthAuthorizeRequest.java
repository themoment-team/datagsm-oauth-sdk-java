package team.themoment.datagsm.sdk.oauth.model;

/**
 * OAuth Authorize 제출 요청 (POST /v1/oauth/authorize)
 */
public class OAuthAuthorizeRequest {
    private String email;
    private String password;

    public OAuthAuthorizeRequest() {}

    public OAuthAuthorizeRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "OAuthAuthorizeRequest{" +
                "email='" + email + '\'' +
                ", password='[REDACTED]'" +
                '}';
    }
}

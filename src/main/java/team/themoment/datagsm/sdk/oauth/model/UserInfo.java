package team.themoment.datagsm.sdk.oauth.model;

/**
 * 사용자 정보
 */
public class UserInfo {
    private Long id;
    private String email;
    private AccountRole role;
    private Boolean isStudent;
    private Student student;

    public UserInfo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public Boolean getIsStudent() {
        return isStudent;
    }

    public void setIsStudent(Boolean isStudent) {
        this.isStudent = isStudent;
    }

    /**
     * 학생 여부 확인
     *
     * @return 학생인 경우 true
     */
    public boolean isStudent() {
        return Boolean.TRUE.equals(isStudent);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isStudent=" + isStudent +
                ", student=" + student +
                '}';
    }
}

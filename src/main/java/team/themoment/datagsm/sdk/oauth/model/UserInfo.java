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
    private AccountStatus status;
    private AccountObjectType objectType;
    private Teacher teacher;

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

    /**
     * @deprecated {@link #getObjectType()}이(가) {@code STUDENT}인지로 대체되었습니다. 하위 호환을 위해 유지됩니다.
     */
    @Deprecated
    public Boolean getIsStudent() {
        return isStudent;
    }

    /**
     * @deprecated {@link #setObjectType(AccountObjectType)}으로 대체되었습니다. 하위 호환을 위해 유지됩니다.
     */
    @Deprecated
    public void setIsStudent(Boolean isStudent) {
        this.isStudent = isStudent;
    }

    /**
     * 학생 여부 확인
     *
     * @return 학생인 경우 true
     * @deprecated {@link #getObjectType()}을(를) 사용하세요.
     */
    @Deprecated
    public boolean isStudent() {
        return Boolean.TRUE.equals(isStudent);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public AccountObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(AccountObjectType objectType) {
        this.objectType = objectType;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isStudent=" + isStudent +
                ", student=" + student +
                ", status=" + status +
                ", objectType=" + objectType +
                ", teacher=" + teacher +
                '}';
    }
}

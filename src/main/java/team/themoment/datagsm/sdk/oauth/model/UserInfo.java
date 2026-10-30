package team.themoment.datagsm.sdk.oauth.model;

import java.util.Collections;
import java.util.List;

/**
 * 사용자 정보 (서버 AccountInfoResDto, {@code GET /userinfo})
 * <p>
 * 액세스 토큰에 부여된 scope에 따라 채워지는 필드가 다릅니다. ({@code {appId}}는 DataGSM 애플리케이션 ID)
 * <ul>
 *     <li>{@code {appId}:account_read} — sub, id, email, role, status, objectType, isStudent (userinfo 호출에 필요).
 *     sub는 이 클레임을 내려주지 않는 이전 서버에서는 null</li>
 *     <li>{@code {appId}:student_read} — student, teacher. 없으면 둘 다 null</li>
 *     <li>{@code {appId}:club_read} — clubs, student.majorClub, student.autonomousClub. 없으면 빈 목록/null</li>
 *     <li>{@code {appId}:project_read} — projects. 없으면 빈 목록</li>
 * </ul>
 * 하위 호환용 {@code {appId}:self_read}는 account_read + student_read와 같게 취급됩니다.
 * scope가 있어도 계정 종류에 따라 student 또는 teacher 중 하나만 채워지고, 연결 대상이 없으면 objectType과 함께 null입니다.
 * <p>
 * 서버가 SDK에 없는 enum 값을 새로 내려주면 해당 필드는 예외 없이 null로 역직렬화됩니다.
 */
public class UserInfo {
    private String sub;
    private Long id;
    private String email;
    private AccountRole role;
    private Boolean isStudent;
    private Student student;
    private AccountStatus status;
    private AccountObjectType objectType;
    private Teacher teacher;
    private List<ClubSummary> clubs;
    private List<ProjectSummary> projects;

    public UserInfo() {}

    /**
     * OIDC 사용자 식별자 ({@code sub} 클레임). id_token, 액세스 토큰의 {@code sub}와 같은 값(email)입니다.
     * <p>
     * 이 필드를 내려주지 않는 이전 서버에서는 null입니다.
     */
    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

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

    /**
     * 학생 정보. 학생 계정이 아니거나 {@code student_read} scope가 없으면 null입니다.
     */
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

    /**
     * 선생님 정보. 선생님 계정이 아니거나 {@code student_read} scope가 없으면 null입니다.
     */
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    /**
     * 소속 동아리 목록. {@code club_read} scope가 없으면 빈 목록입니다. null을 반환하지 않습니다.
     */
    public List<ClubSummary> getClubs() {
        return clubs != null ? Collections.unmodifiableList(clubs) : Collections.emptyList();
    }

    public void setClubs(List<ClubSummary> clubs) {
        this.clubs = clubs;
    }

    /**
     * 참여 프로젝트 목록. {@code project_read} scope가 없으면 빈 목록입니다. null을 반환하지 않습니다.
     */
    public List<ProjectSummary> getProjects() {
        return projects != null ? Collections.unmodifiableList(projects) : Collections.emptyList();
    }

    public void setProjects(List<ProjectSummary> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "sub='" + sub + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isStudent=" + isStudent +
                ", student=" + student +
                ", status=" + status +
                ", objectType=" + objectType +
                ", teacher=" + teacher +
                ", clubs=" + clubs +
                ", projects=" + projects +
                '}';
    }
}

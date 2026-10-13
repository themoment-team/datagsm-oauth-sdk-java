package team.themoment.datagsm.sdk.oauth.model;

/**
 * 학생 정보 (서버 StudentResDto)
 * <p>
 * {@code majorClub}/{@code autonomousClub}은 토큰에 {@code club_read} scope가 없으면 null입니다.
 * grade, classNum 등 학적 정보는 졸업생·자퇴생이면 null일 수 있습니다.
 */
public class Student {
    private Long id;
    private String name;
    private Sex sex;
    private String email;
    private Integer grade;
    private Integer classNum;
    private Integer number;
    private Integer studentNumber;
    private Major major;
    private StudentRole role;
    private Integer dormitoryFloor;
    private Integer dormitoryRoom;
    private ClubSummary majorClub;
    private ClubSummary autonomousClub;
    private String specialty;
    private String githubId;
    private String githubUrl;

    public Student() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getClassNum() {
        return classNum;
    }

    public void setClassNum(Integer classNum) {
        this.classNum = classNum;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public StudentRole getRole() {
        return role;
    }

    public void setRole(StudentRole role) {
        this.role = role;
    }

    public Integer getDormitoryFloor() {
        return dormitoryFloor;
    }

    public void setDormitoryFloor(Integer dormitoryFloor) {
        this.dormitoryFloor = dormitoryFloor;
    }

    public Integer getDormitoryRoom() {
        return dormitoryRoom;
    }

    public void setDormitoryRoom(Integer dormitoryRoom) {
        this.dormitoryRoom = dormitoryRoom;
    }

    /**
     * 자퇴 여부 확인
     * <p>
     * 서버 응답에는 isLeaveSchool 필드가 없으며, 자퇴 여부는 {@code role == StudentRole.WITHDRAWN}으로 판단합니다.
     *
     * @return 역할이 {@link StudentRole#WITHDRAWN}이면 true
     * @deprecated {@code getRole() == StudentRole.WITHDRAWN}을 사용하세요.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public boolean isLeaveSchool() {
        return role == StudentRole.WITHDRAWN;
    }

    /**
     * 전공 동아리. 소속이 없거나 {@code club_read} scope가 없으면 null입니다.
     */
    public ClubSummary getMajorClub() {
        return majorClub;
    }

    public void setMajorClub(ClubSummary majorClub) {
        this.majorClub = majorClub;
    }

    /**
     * 자율(창체) 동아리. 소속이 없거나 {@code club_read} scope가 없으면 null입니다.
     */
    public ClubSummary getAutonomousClub() {
        return autonomousClub;
    }

    public void setAutonomousClub(ClubSummary autonomousClub) {
        this.autonomousClub = autonomousClub;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", email='" + email + '\'' +
                ", grade=" + grade +
                ", classNum=" + classNum +
                ", number=" + number +
                ", studentNumber=" + studentNumber +
                ", major=" + major +
                ", specialty='" + specialty + '\'' +
                ", role=" + role +
                ", dormitoryFloor=" + dormitoryFloor +
                ", dormitoryRoom=" + dormitoryRoom +
                ", majorClub=" + majorClub +
                ", autonomousClub=" + autonomousClub +
                ", githubId='" + githubId + '\'' +
                ", githubUrl='" + githubUrl + '\'' +
                '}';
    }
}

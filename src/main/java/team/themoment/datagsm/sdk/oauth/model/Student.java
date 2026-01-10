package team.themoment.datagsm.sdk.oauth.model;

/**
 * 학생 정보
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
    private Boolean isLeaveSchool;
    private ClubInfo majorClub;
    private ClubInfo jobClub;
    private ClubInfo autonomousClub;

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

    public Boolean getIsLeaveSchool() {
        return isLeaveSchool;
    }

    public void setIsLeaveSchool(Boolean isLeaveSchool) {
        this.isLeaveSchool = isLeaveSchool;
    }

    /**
     * 자퇴 여부 확인
     *
     * @return 자퇴한 경우 true
     */
    public boolean isLeaveSchool() {
        return Boolean.TRUE.equals(isLeaveSchool);
    }

    public ClubInfo getMajorClub() {
        return majorClub;
    }

    public void setMajorClub(ClubInfo majorClub) {
        this.majorClub = majorClub;
    }

    public ClubInfo getJobClub() {
        return jobClub;
    }

    public void setJobClub(ClubInfo jobClub) {
        this.jobClub = jobClub;
    }

    public ClubInfo getAutonomousClub() {
        return autonomousClub;
    }

    public void setAutonomousClub(ClubInfo autonomousClub) {
        this.autonomousClub = autonomousClub;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", email='" + email + '\'' +
                ", grade=" + grade +
                ", classNum=" + classNum +
                ", number=" + number +
                ", studentNumber=" + studentNumber +
                ", major=" + major +
                ", role=" + role +
                ", dormitoryFloor=" + dormitoryFloor +
                ", dormitoryRoom=" + dormitoryRoom +
                ", isLeaveSchool=" + isLeaveSchool +
                ", majorClub=" + majorClub +
                ", jobClub=" + jobClub +
                ", autonomousClub=" + autonomousClub +
                '}';
    }
}

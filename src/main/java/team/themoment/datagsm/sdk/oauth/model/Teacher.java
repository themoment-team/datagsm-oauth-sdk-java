package team.themoment.datagsm.sdk.oauth.model;

/**
 * 선생님 정보
 */
public class Teacher {
    private Long id;
    private String name;
    private String email;
    private TeacherDepartment department;
    private String description;

    public Teacher() {}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TeacherDepartment getDepartment() {
        return department;
    }

    public void setDepartment(TeacherDepartment department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department=" + department +
                ", description='" + description + '\'' +
                '}';
    }
}

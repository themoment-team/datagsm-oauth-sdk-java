package team.themoment.datagsm.sdk.oauth.model;

/**
 * 프로젝트 요약 정보 (서버 ProjectSummaryDto)
 */
public class ProjectSummary {
    private Long id;
    private String name;
    private ProjectStatus status;
    private ClubSummary club;

    public ProjectSummary() {}

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

    /**
     * 프로젝트 상태. SDK가 모르는 값이 내려오면 null입니다.
     */
    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    /**
     * 소속 동아리. 동아리에 속하지 않은 프로젝트는 null입니다.
     */
    public ClubSummary getClub() {
        return club;
    }

    public void setClub(ClubSummary club) {
        this.club = club;
    }

    @Override
    public String toString() {
        return "ProjectSummary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", club=" + club +
                '}';
    }
}

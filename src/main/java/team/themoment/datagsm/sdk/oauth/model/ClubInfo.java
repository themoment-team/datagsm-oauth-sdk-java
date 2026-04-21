package team.themoment.datagsm.sdk.oauth.model;

/**
 * 동아리 정보
 */
public class ClubInfo {
    private Long id;
    private String name;
    private ClubType type;
    private ClubStatus status;
    private Integer foundedYear;
    private Integer abolishedYear;

    public ClubInfo() {}

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

    public ClubType getType() {
        return type;
    }

    public void setType(ClubType type) {
        this.type = type;
    }

    public ClubStatus getStatus() {
        return status;
    }

    public void setStatus(ClubStatus status) {
        this.status = status;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public Integer getAbolishedYear() {
        return abolishedYear;
    }

    public void setAbolishedYear(Integer abolishedYear) {
        this.abolishedYear = abolishedYear;
    }

    @Override
    public String toString() {
        return "ClubInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", foundedYear=" + foundedYear +
                ", abolishedYear=" + abolishedYear +
                '}';
    }
}

package team.themoment.datagsm.sdk.oauth.model;

/**
 * 동아리 정보
 */
public class ClubInfo {
    private Long id;
    private String name;
    private ClubType type;

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

    @Override
    public String toString() {
        return "ClubInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

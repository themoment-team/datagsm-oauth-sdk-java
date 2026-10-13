package team.themoment.datagsm.sdk.oauth.model;

/**
 * 동아리 요약 정보 (서버 ClubSummaryDto)
 * <p>
 * UserInfo의 {@code clubs}, {@code student.majorClub}/{@code student.autonomousClub},
 * {@code projects[].club}에서 사용됩니다.
 */
public class ClubSummary {
    private Long id;
    private String name;
    private ClubType type;

    public ClubSummary() {}

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
     * 동아리 종류. SDK가 모르는 값이 내려오면 null입니다.
     */
    public ClubType getType() {
        return type;
    }

    public void setType(ClubType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ClubSummary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

package team.themoment.datagsm.sdk.oauth.client;

import team.themoment.datagsm.sdk.oauth.model.UserInfo;

/**
 * Account API 인터페이스
 */
public interface AccountApi {

    /**
     * Access Token으로 사용자 정보 조회
     *
     * @param accessToken Access Token
     * @return 사용자 정보
     */
    UserInfo getUserInfo(String accessToken);
}

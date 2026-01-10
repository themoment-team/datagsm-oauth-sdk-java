package team.themoment.datagsm.sdk.oauth.client;

import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

/**
 * OAuth API 인터페이스
 */
public interface OAuthApi {

    /**
     * Authorization Code를 Access Token과 Refresh Token으로 교환
     *
     * @param code Authorization Code
     * @return 토큰 응답
     */
    TokenResponse exchangeToken(String code);

    /**
     * Refresh Token으로 새로운 토큰 발급
     *
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 응답
     */
    TokenResponse refreshToken(String refreshToken);
}

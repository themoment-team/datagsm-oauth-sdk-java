package team.themoment.datagsm.sdk.oauth.http;

import okhttp3.*;
import team.themoment.datagsm.sdk.oauth.exception.*;
import team.themoment.datagsm.sdk.oauth.model.CommonApiResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 기반 HTTP 클라이언트 구현
 */
public class OkHttpClientImpl implements HttpClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    public OkHttpClientImpl() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public OkHttpClientImpl(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public String get(String url, Map<String, String> headers, Map<String, String> queryParams) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (queryParams != null) {
            queryParams.forEach((key, value) -> {
                if (value != null) {
                    urlBuilder.addQueryParameter(key, value);
                }
            });
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        return execute(requestBuilder.build());
    }

    @Override
    public String post(String url, Map<String, String> headers, String body) {
        RequestBody requestBody = RequestBody.create(body, JSON);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);

        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        return execute(requestBuilder.build());
    }

    @Override
    public String put(String url, Map<String, String> headers, String body) {
        RequestBody requestBody = RequestBody.create(body, JSON);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .put(requestBody);

        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        return execute(requestBuilder.build());
    }

    private String execute(Request request) {
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                handleErrorResponse(response.code(), responseBody);
            }

            return responseBody;
        } catch (IOException e) {
            throw new DataGsmException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    private void handleErrorResponse(int statusCode, String responseBody) {
        String message = responseBody != null && !responseBody.isEmpty()
                ? responseBody
                : "HTTP " + statusCode + " error";

        switch (statusCode) {
            case 400:
                throw new BadRequestException(message);
            case 401:
                throw new UnauthorizedException(message);
            case 403:
                throw new ForbiddenException(message);
            case 404:
                throw new NotFoundException(message);
            case 429:
                throw new RateLimitException(message);
            case 500:
            case 502:
            case 503:
            case 504:
                throw new ServerErrorException(message);
            default:
                throw new DataGsmException(message, statusCode);
        }
    }

    @Override
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}

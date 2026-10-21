package team.themoment.datagsm.sdk.oauth.http;

import okhttp3.*;
import team.themoment.datagsm.sdk.oauth.exception.DataGsmException;

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
        HttpUrl parsedUrl = HttpUrl.parse(url);
        if (parsedUrl == null) {
            throw new DataGsmException("Invalid URL: " + url);
        }
        HttpUrl.Builder urlBuilder = parsedUrl.newBuilder();

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
                throw ErrorResponseParser.toException(response.code(), responseBody);
            }

            return responseBody;
        } catch (IOException e) {
            throw new DataGsmException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}
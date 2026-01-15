package com.ddlab.rnd.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import com.ddlab.rnd.git.model.GitOnlineResponse;
import com.ddlab.rnd.git.model.UserAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil {

	public static String getEncodedUser(String userName, String token) {
		String endodedString = null;
		String userNamePassword = userName + ":" + token;
		endodedString = Base64.getEncoder().encodeToString(userNamePassword.getBytes());
		return endodedString;
	}

	public static HttpGet getHttpGet(String uri, UserAccount userAccount) {
		HttpGet httpGet = new HttpGet(uri);
		String encodedUser = getEncodedUser(userAccount.getUserName(), userAccount.getToken());
//		System.out.print("Encoded User: " + encodedUser);
		httpGet.setHeader("Authorization", "Basic " + encodedUser);
		return httpGet;
	}

	public static HttpPost getHttpPost(String uri, UserAccount userAccount) {
		HttpPost httpPost = new HttpPost(uri);
		String encodedUser = getEncodedUser(userAccount.getUserName(), userAccount.getToken());
		httpPost.setHeader("Authorization", "Basic " + encodedUser);
		return httpPost;
	}

	public static GitOnlineResponse getHttpGetOrPostResponse(ClassicHttpRequest httpGetOrPost) throws Exception {

		try (CloseableHttpClient httpClient = getTrustedHttpClient()) {
			HttpClientResponseHandler<GitOnlineResponse> responseHandler = (ClassicHttpResponse response) -> {
				int statusCode = response.getCode();
//				log.debug("Status Code: {}",statusCode);
				String responseBody = response.getEntity() != null
						? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
						: "";
//				log.debug("Response Body: {}", responseBody);
				GitOnlineResponse httpResponse = new GitOnlineResponse(statusCode, responseBody);
//				log.debug("Raw Response Bode: {}", httpResponse);
//				return new GitOnlineResponse(statusCode, responseBody);
				return httpResponse;
			};

			return httpClient.execute(httpGetOrPost, responseHandler);
		}
	}

	public static CloseableHttpClient getTrustedHttpClient() {
		try {
			SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
					.build();

			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
					NoopHostnameVerifier.INSTANCE);

			PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(sslSocketFactory).build();

			return HttpClients.custom().setConnectionManager(cm).build();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

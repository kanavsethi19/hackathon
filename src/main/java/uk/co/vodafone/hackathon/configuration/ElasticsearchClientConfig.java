package uk.co.vodafone.hackathon.configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kanav.sethi
 *
 */
@Configuration
public class ElasticsearchClientConfig {
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchClientConfig.class);

	private RestClient lowLevelClient;
	private RestHighLevelClient highLevelClient;

	@Value("${elasticsearch.host}")
	String url;

	@Value("${ES_PORT}")
	int port;

	@Value("${ES_PROTOCOL}")
	String protocol;

	@PostConstruct
	public void init() throws UnknownHostException, MalformedURLException {
		RestClientBuilder builder = null;
		InetAddress address = InetAddress.getByName(new URL(url).getHost());
		builder = RestClient.builder(new HttpHost(address, port, protocol))
				.setRequestConfigCallback(new RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(50000).setSocketTimeout(600000)
								.setConnectionRequestTimeout(50000);
					}
				});
		builder.setMaxRetryTimeoutMillis(100000);
		logger.info("Building Elastic Search Client! ");
		lowLevelClient = builder.build();
		highLevelClient = new RestHighLevelClient(builder);
	}

	@PreDestroy
	public void destroy() throws IOException {
		lowLevelClient.close();
		highLevelClient.close();
		logger.info("Closing Elastic Search Client:::");
	}

	@Bean
	public RestClient getRestClient() {
		return lowLevelClient;
	}

	@Bean
	public RestHighLevelClient getHighLevelRestclient() {
		return highLevelClient;
	}

}

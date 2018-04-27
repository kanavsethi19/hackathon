package uk.co.vodafone.hackathon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


/**
 * @author kanav.sethi
 *
 */
@Service
public class ESQueryingImpl implements ESQuerying {
	private static final Logger logger = LoggerFactory.getLogger(ESQueryingImpl.class);
	
	@Autowired
	private RestHighLevelClient client;
	
	@Autowired
	private Environment enviroment;
	
	@Override
	public void getDataForQueryString(String query) {
		String indexName = enviroment.getProperty("Index");
		String type = enviroment.getProperty("Type");
		try {
			String[] fieldNames = {"productName","displayName"};
			QueryBuilder builder = QueryBuilders.multiMatchQuery(query, fieldNames).fuzziness("AUTO").operator(Operator.AND);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(builder);
			sourceBuilder.from(0);
			sourceBuilder.size(10);
			SearchRequest request = new SearchRequest(indexName);
			request.types(type);
			request.source(sourceBuilder);
			SearchResponse searchResponse = client.search(request);
			List<String> responseFromES = new ArrayList<>();
			for (SearchHit hit : searchResponse.getHits().getHits()) {
				responseFromES.add(hit.getId());
				logger.info(hit.getSourceAsString());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void getDataFromESById(String query) {
		List<String> bundleIds = null;
		String indexName = enviroment.getProperty("Index");
		String type = enviroment.getProperty("Type");
		try {
			bundleIds = new ArrayList<>();
			bundleIds = Arrays.asList("opt_product_200327");
			String[] arr = bundleIds.toArray(new String[bundleIds.size()]);
			logger.info("Total Bundles in CSV FILE: " + arr.length);
			QueryBuilder builder = QueryBuilders.idsQuery().addIds(arr);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(builder);
			sourceBuilder.from(0);
			String[] includeFields = new String[] { "id", "deviceSpecificPricing.*" };
			sourceBuilder.fetchSource(includeFields, null);
			sourceBuilder.size(bundleIds.size());
			SearchRequest request = new SearchRequest(indexName);
			request.types(type);
			request.source(sourceBuilder);
			SearchResponse searchResponse = client.search(request);
			logger.info("Total Documents in response from elasticsearch: " + searchResponse.getHits().totalHits);
			List<String> responseFromES = new ArrayList<>();
			for (SearchHit hit : searchResponse.getHits().getHits()) {
				responseFromES.add(hit.getId());
				logger.info(hit.getSourceAsString());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}

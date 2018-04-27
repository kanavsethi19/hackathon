package uk.co.vodafone.hackathon.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ESIndexingImpl implements ESIndexing {
	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private RestClient lowLevelClient;

	@Autowired
	private Environment enviroment;

	@Override
	public void loadDataToES() {
		try {
//			createIndex();
//			indexQuery();
			 checkDocumentAgainstQueries();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkDocumentAgainstQueries() throws IOException {
		/*
		 * XContentBuilder xContentBuilder = jsonBuilder().startObject();
		 * xContentBuilder.field("brand","apple"); xContentBuilder.endObject();
		 * PercolateQueryBuilder percolateQueryBuilder = new
		 * PercolateQueryBuilder("query","doc", xContentBuilder.bytes(),
		 * XContentType.JSON);
		 */
		String indexName = "ecomm";
		String query = "{" + "    \"query\" : {" + "        \"percolate\" : {" + "            \"field\" : \"query\","
				+ "            \"document\" : {" + "                \"brand\":\"samsung\","
				+ "                \"price\" : 90000" + "            }" + "        }" + "    }" + "}";
		HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("pretty", "true");
		                               
		org.elasticsearch.client.Response response = lowLevelClient.performRequest("GET", "/" + indexName + "/_search",
				paramMap, entity);
		System.out.println(EntityUtils.toString(response.getEntity()));

		System.out.println("Querying doc!");

	}

	public void indexQuery(String userName,String phoneNumber,String emaildId,String id,String requirement) throws IOException {
		/*TermQueryBuilder builder = QueryBuilders.termQuery("brand", "apple");
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("brand", "apple");*/
		Map<String, String> map = traverseRequirement(requirement);
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("brand", map.get("brand")).fuzziness(Fuzziness.AUTO))
				.must(QueryBuilders.rangeQuery("price").gte(map.get("gte")).lte(map.get("lte")))
				.should(QueryBuilders.matchQuery("username", userName))
				.should(QueryBuilders.matchQuery("phonenumber", phoneNumber)).should(QueryBuilders.matchQuery("emailid", emaildId));

		XContentBuilder xContentBuilder = jsonBuilder().startObject().field("query", boolQueryBuilder).endObject();
		IndexRequest indexRequest = new IndexRequest("ecomm", "doc", id).source(xContentBuilder);
		IndexResponse indexResponse = client.index(indexRequest);
		System.out.println(indexResponse.status());
		System.out.println("Query indexed!");
	}
	
	public String createIndex() throws IOException {
		String indexName = null;
		try {
			indexName = "ecomm";
			String mapping = "{" + 
					"  \"mappings\": {" + 
					"   \"doc\":{" + 
					"      \"properties\":{" + 
					"      \"brand\" :{\"type\":\"text\"}," + 
					"      \"username\":{\"type\": \"text\"}," + 
					"      \"price\" :{\"type\":\"integer\"}," + 
					"      \"phonenumber\":{\"type\": \"text\"}," + 
					"      \"emailid\":{\"type\": \"text\"}," + 
					"      \"query\": {" + 
					"          \"type\": \"percolator\"" + 
					"      }" + 
					"    }" + 
					"   }" + 
					"  }" + 
					"}";
			HttpEntity entity = new NStringEntity(mapping, ContentType.APPLICATION_JSON);
			org.elasticsearch.client.Response response = lowLevelClient.performRequest("PUT", "/" + indexName,
					Collections.emptyMap(), entity);
			System.out.println("Index created!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexName;
	}
	
	public Boolean checkIfIndexExists(String name) throws Exception {
		Boolean exists = false;
		try {
			org.elasticsearch.client.Response response1 = lowLevelClient.performRequest("HEAD", "/" + name);
			StatusLine statusLine = response1.getStatusLine();
			if (statusLine != null && statusLine.getStatusCode() == 200)
				exists = true;

		} catch (Exception e) {
			throw e;
		}
		return exists;

	}

	@Override
	public Map<String, String> traverseRequirement(String requirement) {
		Map<String, String> map = new HashMap<>();
		if(requirement.contains("apple") || requirement.contains("Apple"))
			map.put("brand","apple iphone");
		else if(requirement.contains("samsung") || requirement.contains("Samsung"))
			map.put("brand", "samsung phone");
		if(requirement.contains("under")) {
			String[] arr = requirement.split(" ");
			String price = null;
			for(int i=1;i<arr.length;i++) {
				if(arr[i].equals("under")) {
					price= arr[i+1];
				}
			}
			map.put("lte",price);
		}
		return map;
	}

}

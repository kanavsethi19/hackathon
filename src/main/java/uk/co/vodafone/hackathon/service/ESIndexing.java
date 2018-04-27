package uk.co.vodafone.hackathon.service;

import java.io.IOException;
import java.util.Map;

public interface ESIndexing {
	public void loadDataToES();
	public void checkDocumentAgainstQueries() throws IOException;
	public void indexQuery(String userName,String phoneNumber,String emaildId,String id,String requirement) throws IOException;
	public String createIndex() throws IOException;
	public Boolean checkIfIndexExists(String name) throws Exception; 
	public Map<String, String> traverseRequirement(String requirement);
}

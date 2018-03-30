package uk.co.vodafone.hackathon.service;

/**
 * @author kanav.sethi
 *
 */
public interface ESQuerying {
	public void getDataFromESById(String query);
	public void getDataForQueryString(String query);
}

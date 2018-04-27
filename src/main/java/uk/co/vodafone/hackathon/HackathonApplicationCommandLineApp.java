/**

 */
package uk.co.vodafone.hackathon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.co.vodafone.hackathon.service.ESIndexing;
import uk.co.vodafone.hackathon.service.ESQuerying;


/**
 * @author kanav.sethi
 *
 */
//@SpringBootApplication
public class HackathonApplicationCommandLineApp implements CommandLineRunner{
	private static final Logger logger = LoggerFactory.getLogger(HackathonApplicationCommandLineApp.class);
	
	@Autowired
	private ESQuerying esQuerying;
	
	@Autowired
	private ESIndexing esIndexing;
	
	public static void main(String[] args) {
		SpringApplication.run(HackathonApplicationCommandLineApp.class, args).close();
	}

	@Override
	public void run(String... arg0) throws Exception {
		logger.info("Starting to index!");
		esIndexing.loadDataToES();
		logger.info("Done!");
	}
}

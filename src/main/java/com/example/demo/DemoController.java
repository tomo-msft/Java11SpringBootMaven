package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DemoController {
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/")
    public String index () {
        return "index";
    }

    @GetMapping("/log")
    public String logTest (
		@RequestParam(defaultValue = "Log Test") String name, Model result) {

		Date now = new Date();

		logger.debug("Debug log test " + name + " : " + now);
		logger.info("Info log test " + name + " : " + now);
		logger.warn("Warn log test " + name + " : " + now);
		logger.error("Error log test " + name + " : " + now);

		result.addAttribute("date", now);
		result.addAttribute("status", "Success");
		Map<String, String> description = new HashMap<>();
		description.put("Test Name", name);
		result.addAttribute("description", description);
        return "testResult";
    }

    @GetMapping("/exception")
    public String exceptionTest (
		@RequestParam(defaultValue = "Exception Test") String name, Model result) {

		Date now = new Date();
		Exception ex = new Exception("Sample Exception");
		logger.debug("Debug log with Exception " + name + " : " + now, ex);
		logger.info("Info log with Exception " + name + " : " + now, ex);
		logger.warn("Warn log with Exception " + name + " : " + now, ex);
		logger.error("Error log with Exception " + name + " : " + now, ex);


		String str = null;
		int len = str.length();

		result.addAttribute("date", now);
		result.addAttribute("status", "Success");
		Map<String, String> description = new HashMap<>();
		description.put("Test Name", name);
		result.addAttribute("description", description);
        return "testResult";
    }

	@GetMapping("/dependency")
    public String dependencyTest (
		@RequestParam(defaultValue = "Dependency Test") String name, 
		@RequestParam(defaultValue = "https://www.bing.com") String url, 
		Model result) {

		Date now = new Date();
		Map<String, String> description = new HashMap<>();
		description.put("Test Name", name);
		description.put("URL", url);


		logger.info("Dependency test \"" + name + "\" , url " + url + " start at " + now);
		HttpURLConnection  urlConn = null;
		InputStream in = null;
		BufferedReader reader = null;

		try {
			URL urlObj = new URL(url);
			urlConn = (HttpURLConnection) urlObj.openConnection();
			urlConn.setRequestMethod("GET");
//			urlConn.setRequestMethod("POST");
			urlConn.connect();

			int status = urlConn.getResponseCode();

			int length = 0;
			StringBuilder output = new StringBuilder();
			if (status == HttpURLConnection.HTTP_OK) {
				in = urlConn.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line);
				}
				length = output.toString().length();
			}

			logger.info("HTTP Status:" + status + ", Content Length:" + length);			

			result.addAttribute("status", "Success");
			description.put("HTTP Status", String.valueOf(status));
			description.put("Content Lenght", String.valueOf(length));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("HTTP Connection failed", e);

			result.addAttribute("status", "Failed");
			description.put("Exception Message", e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (urlConn != null) {
					urlConn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("HTTP Connection finalizer failed", e);
			}

		}
	
		result.addAttribute("description", description);
		result.addAttribute("date", now);
		return "testResult";
    }

}

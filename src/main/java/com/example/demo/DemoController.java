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

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import com.microsoft.applicationinsights.telemetry.SeverityLevel;

@Controller
public class DemoController {
	static final TelemetryClient telemetryClient = new TelemetryClient();
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
	private static final String AppName = "Java11SpringBootMaven-AiAuto01";

	private String createLog(String message, String testName, Date now) {
		return String.format(message + " TestName:" + testName + " AppName: " + AppName + " Date: " + now);
	}

	private void setResult(Model result, String testName, Date now) {
		result.addAttribute("appName", AppName);
		result.addAttribute("testName", testName);
		result.addAttribute("date", now);
	}

	@GetMapping("/")
    public String index () {
        return "index";
    }

    @GetMapping("/log")
    public String logTest (
		@RequestParam(defaultValue = "Log Test") String name, Model model) {

		Date now = new Date();

		// do test
		logger.debug(createLog("Debug log test", name, now));
		logger.info(createLog("Info log test", name, now));
		logger.warn(createLog("Warn log test", name, now));
		logger.error(createLog("Error log test", name, now));

		// set result
		setResult(model, name, now);
		Map<String, String> description = new HashMap<>();
		description.put("status", "Success");
		model.addAttribute("description", description);
        return "testResult";
    }

    @GetMapping("/exception")
    public String exceptionTest (
		@RequestParam(defaultValue = "Exception Test") String name, Model model) {

		Date now = new Date();

		// do test
		Exception ex = new Exception("Sample Exception");
		logger.debug(createLog("Debug log with Exception ", name, now), ex);
		logger.info(createLog("Info log with Exception ", name, now), ex);
		logger.warn(createLog("Warn log with Exception ", name, now), ex);
		logger.error(createLog("Error log with Exception ", name, now), ex);

		// create unhandled exception
		String str = null;
		int len = str.length();

		// set result
		setResult(model, name, now);
		Map<String, String> description = new HashMap<>();
		description.put("status", "Success");
		model.addAttribute("description", description);
        return "testResult";
    }

	@GetMapping("/dependency")
    public String dependencyTest (
		@RequestParam(defaultValue = "Dependency Test") String name, 
		@RequestParam(defaultValue = "https://www.bing.com") String url, 
		Model model) {

		Date now = new Date();

		// do test
		Map<String, String> description = new HashMap<>();
		description.put("Test Name", name);
		description.put("URL", url);

		logger.info(createLog("Dependency test " + url, name, now));
		HttpURLConnection  urlConn = null;
		InputStream in = null;
		BufferedReader reader = null;

		try {
			URL urlObj = new URL(url);
			urlConn = (HttpURLConnection) urlObj.openConnection();
			urlConn.setRequestMethod("GET");
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

			// set result
			description.put("status", "Success");
			description.put("HTTP Status", String.valueOf(status));
			description.put("Content Lenght", String.valueOf(length));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(createLog("HTTP Connection failed " + url, name, now), e);

			// set result
			description.put("status", "Failed");
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

		setResult(model, name, now);
		model.addAttribute("description", description);
		return "testResult";
    }

    @GetMapping("/sdk")
    public String sdkTest (
		@RequestParam(defaultValue = "SDK_Test") String name, Model model) {

		Date now = new Date();
		
		// Event test
		telemetryClient.trackEvent("Sample Event");
		
		// Metric test
		telemetryClient.trackMetric("SampleMetric", 42.0);

		// Dependency test
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis() + 100;
		RemoteDependencyTelemetry telemetry = new RemoteDependencyTelemetry();
		telemetry.setName("SampleName");
		telemetry.setResultCode("499");
		telemetry.setTarget("SampleTarget");
		telemetry.setType("SampleType");
		telemetry.setCommandName("SampleCommandName");
		telemetry.setSuccess(false);
		telemetry.setTimestamp(new Date(startTime));
		telemetry.setDuration(new Duration(endTime - startTime));
		telemetryClient.trackDependency(telemetry);

		// Log test
		Map<String, String> properties = new HashMap<>();
		properties.put("Key1", "Value1");
		properties.put("Key2", "Value2");
		telemetryClient.trackTrace(createLog("Sample log", name, now), SeverityLevel.Warning, properties);

		// Exception test
		Exception ex = new Exception("Sample Exception");
		telemetryClient.trackException(ex);

		// set result
		setResult(model, name, now);
		Map<String, String> description = new HashMap<>();
		description.put("status", "Success");
		model.addAttribute("description", description);
        return "testResult";
    }

}

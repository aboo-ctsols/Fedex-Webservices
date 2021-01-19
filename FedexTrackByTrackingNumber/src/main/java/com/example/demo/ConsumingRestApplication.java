package com.example.demo;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SpringBootApplication
public class ConsumingRestApplication {

	private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);

	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(ConsumingRestApplication.class, args);
		getTrackingByTrackingNumber();
	}
	
	public static String getBearerAccessToken() throws IOException, ParseException {
		OkHttpClient client = new OkHttpClient();
				MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
				RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=l734c39c42ce1f4dc9a9ac4e1e533ce126&client_secret=b37bb00a3abc464f91d4749ca41e6562");
				Request request = new Request.Builder()
				  .url("https://apis-sandbox.fedex.com/oauth/token")
				  .method("POST", body)
				  .addHeader("Content-Type", "application/x-www-form-urlencoded")
				  .build();
				Response response = client.newCall(request).execute();
				JSONParser parse = new JSONParser();
				JSONObject bearerAccessToken = (JSONObject)parse.parse(response.body().string());
		return (String) bearerAccessToken.get("access_token");
	}
	
	public static void getTrackingByTrackingNumber() throws IOException, ParseException {
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		// 'input' refers to JSON Payload
		String input = "{\n"
				+ "  \"includeDetailedScans\": true,\n"
				+ "  \"trackingInfo\": [\n"
				+ "    {\n"
				+ "      \"trackingNumberInfo\": {\n"
				+ "        \"trackingNumber\": \"568838414941\"\n"
				+ "      }\n"
				+ "    }\n"
				+ "  ]\n"
				+ "}";
		RequestBody body = RequestBody.create(mediaType, input);
		
		String bearerAccessToken = getBearerAccessToken();
		Request request = new Request.Builder()
		    .url("https://apis-sandbox.fedex.com/track/v1/trackingnumbers")
		    .post(body)
		    .addHeader("Content-Type", "application/json")
		    .addHeader("X-locale", "en_US")
		    .addHeader("Authorization", "Bearer "+bearerAccessToken)
		    .build();
		            
		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
		
	}
}

package com.restApi.practice.jsonserver;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Interview {

	RequestSpecification reqSpec;
	ResponseSpecification resSpec;

	@BeforeTest
	public void beforeTest() {
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.setBaseUri("http://localhost:3000");
		reqBuilder.setBasePath("/posts");
		reqSpec = reqBuilder.build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getResponse_writeToFile_readFile_validateResponse_jsonFormat() throws Exception {
		Response response = RestAssured
				.given()
				.spec(reqSpec)
				.log()
				.all()
				.when()
				.get("/1")
				.then().log().all()
				.extract().response();
	    
		// write json
		String path = "./src/test/resources/text.json";
		PrintWriter pw = new PrintWriter(path);
	    pw.write(response.asString());
	    pw.flush();
	    pw.close();
	    
		// read - json
		FileInputStream files = new FileInputStream(path);
		Map<String, Object> expectedJson = new ObjectMapper().readValue(files, Map.class);
		JSONObject json = new JSONObject(expectedJson);
		System.out.println("ID -> " + json.getString("id"));
		System.out.println("Title -> " + json.getString("title"));
		System.out.println("Views -> " + json.getInt("views"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getResponse_writeToFile_readFile_validateResponse_txtFormat() throws Exception {
		Response response = RestAssured
				.given()
				.spec(reqSpec)
				.log()
				.all()
				.when()
				.get("/1")
				.then().log().all()
				.extract().response();
	    
		// write txt
		String path = "./src/test/resources/text.txt";
		PrintWriter pw = new PrintWriter(path);
	    pw.write(response.asString());
	    pw.flush();
	    pw.close();
	    
		// read - txt
		FileInputStream files = new FileInputStream(path);
		Map<String, Object> expectedJson = new ObjectMapper().readValue(files, Map.class);
		JSONObject json = new JSONObject(expectedJson);
		System.out.println("ID -> " + json.getString("id"));
		System.out.println("Title -> " + json.getString("title"));
		System.out.println("Views -> " + json.getInt("views"));
	}
}

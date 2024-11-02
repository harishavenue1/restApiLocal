package com.restApi.practice.jsonserver;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class PostsTest {

	String[] items = { "id", "title", "views" };
	List<String> expectedKeys = List.of("id", "title", "views");
	
	@Test(priority=1)
	public void getAPost() {
		RestAssured.given().log().all().when().get("http://localhost:3000/posts/1").then().log().all().assertThat()
				.body("keySet()", hasItems(items))
				.body("id", allOf(notNullValue(), instanceOf(String.class)))
				.body("title", both(notNullValue()).and(instanceOf(String.class))) 
				.body("views", allOf(notNullValue(), instanceOf(Integer.class)))
				.body("views", greaterThan(1));
	}
	
	@Test(priority=2)
	public void getAllPosts() {
		RestAssured.given().log().all().when().get("http://localhost:3000/posts").then().log().all().assertThat()
				.body("$", hasSize(2))
				.body("[0].keySet()", hasItems(items))
		        .body("every { it.keySet().containsAll(['" + String.join("', '", items) + "']) }", is(true));
	}
	
	@Test(priority=3)
	public void getInvalidPost() {
		RestAssured.given().log().all().when().get("http://localhost:3000/posts/300").then()
				.contentType(ContentType.TEXT).log().all().assertThat()
				.body(is("Not Found"));
	}
	
	@SuppressWarnings("unchecked")
	@Test(priority=4)
	public void createPost() throws JsonMappingException, JsonProcessingException {
		String body = "{\n"
				+ "    \"id\": \"3\",\n"
				+ "    \"title\": \"b title\",\n"
				+ "    \"views\": 300\n"
				+ "  }";

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> expectedJson = objectMapper.readValue(body, Map.class);
		
		RestAssured.given().log().all()
			.body(body)
			.when().post("http://localhost:3000/posts").then()
			.contentType(ContentType.JSON).log().all().assertThat()
			.statusCode(is(201))
			.body("", is(expectedJson));
	}
	
	@Test(priority=5, dependsOnMethods = "createPost")
	public void putPost() {
		String body = "{\n"
				+ "    \"id\": \"3\",\n"
				+ "    \"title\": \"b2 title\",\n"
				+ "    \"views\": 300\n"
				+ "  }";
		RestAssured.given().log().all()
			.body(body)
			.when().put("http://localhost:3000/posts/3").then()
			.log().all().assertThat()
			.statusCode(is(200));
	}
	
	@Test(priority=6, dependsOnMethods = "createPost")
	//@Test(priority=6, invocationCount =  3)
	public void deletePost() {
		RestAssured.given().log().all()
			.when().delete("http://localhost:3000/posts/3").then()
			.log().all().assertThat()
			.statusCode(is(200));
	}
	
	@SuppressWarnings("unchecked")
	@Test(priority=7)
	public void justPost() throws IOException {
		FileInputStream file = new FileInputStream(new File(System.getProperty("user.dir")+"/src/test/resources/ReqBody.json"));
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> expectedJson = objectMapper.readValue(file, Map.class);
		
		Response res = RestAssured.given().log().all()
			.body(expectedJson)
			.when().post("http://localhost:3000/posts").andReturn();
		
		System.out.println(res.path("title").toString());
		assertEquals(res.path("id").toString(), "3");
		assertEquals(res.statusCode(), 201);
	}
}

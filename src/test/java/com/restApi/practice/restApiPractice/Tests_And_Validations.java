package com.restApi.practice.restApiPractice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Tests_And_Validations {

	String baseUrl = "https://reqres.in";
	RequestSpecification requestSpec;

	@BeforeTest
	public void preTest() {
		requestSpec = RestAssured.given().baseUri(baseUrl).log().all();
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Test(priority = 1)
	public void Test_1() {
		// path param
		Response response1 = RestAssured
								.given()
								.spec(requestSpec)
								.pathParam("pageId", 2)
								.when()
								.get("/api/users?page={pageId}")
								.andReturn();

		// query param
		Response response = RestAssured
								.given()
								.spec(requestSpec)
								.queryParam("pageId", 2)
								.when()
								.get("/api/users")
								.andReturn();
		
		System.out.println("Response: " + response.asString());
		assertThat("Status Code Mismatch", response.statusCode(), is(200));

		// validate array count - expected: 7
		assertThat("Response Array Count Mismatch", JsonPath.read(response.asString(), "$.data.length()"), is(6));

		// validate array ID
		List<Integer> ids = List.of(1,2,3,4,5,6);
		List<String> expectedIDs = JsonPath.read(response.asString(), "$.data[*].id");
		assertThat("Response Array ID Mismatch", JsonPath.read(response.asString(), "$.data[*].id"), is(ids));

		// validate array names
		List<String> expectedNames = List.of("George", "Janet");
		List<String> actualNames = ((List<Object>) JsonPath.read(response.asString(), "$.data[*].first_name"))
									.stream()
									.map(Object::toString)
									.filter(expectedNames::contains)
									.collect(Collectors.toList());
		assertThat("Missing Expected FirstNames", actualNames, containsInAnyOrder(expectedNames.toArray()));

		// validate array names from extracting to list
		List<String> actual = JsonPath.read(response.asString(), "$.data[*].first_name");
		System.out.println("Actual " + actual);
		System.out.println("Expected " + expectedNames);
		assertThat("Response Array Data Missing", actual.containsAll(expectedNames));

		// max page
		System.out.println("Expected IDs list " + expectedIDs);
		Double largestPadding = JsonPath.read(response.asString(), "$.max($.data[*].id)");
		assertThat("Max Data mismatch", largestPadding == 6);
	}

	@Test
	public void Test_2() {
		String body = "{\n" + "    \"name\": \"HKP\",\n" + "    \"job\": \"P-SDET\"\n" + "}";
		Response response = RestAssured
								.given()
								.contentType(ContentType.JSON)
								.body(body)
								.spec(requestSpec)
								.when()
								.post("/api/users")
								.andReturn();
		System.out.println("Response " + response.asString());
		assertThat("Status Code Mismatch", response.statusCode(), is(201));
		String responseStr = response.asString();
		assertThat("Item Value Mismatch for ID", JsonPath.read(responseStr, "$.createdAt"), notNullValue());
		assertThat("Datatype Mismatch for ID", JsonPath.read(responseStr, "$.createdAt"), instanceOf(String.class));
		assertThat("Datatype Mismatch for ID", JsonPath.read(responseStr, "$.id"), instanceOf(String.class));
		assertThat("Datatype Mismatch for ID", Integer.valueOf(JsonPath.read(responseStr, "$.id")), greaterThan(1));
	}
	
	@Test
	public void uploadTest() throws Exception
	{
		Response response = RestAssured.given()
								.log().all()
								.multiPart("file", new File("/users/harishkp/downloads/holdings_1.csv"), "multipart/form-data")
								.post("https://the-internet.herokuapp.com/upload")
								.thenReturn();
			
		System.out.println(response.asString());
	}
	
	@Test
	public void downloadTest() throws Exception
	{
		Response response = RestAssured.given()
								.log().all()
								.get("https://reqres.in/api/users")
								.thenReturn();
//		byte[] bytes = response.getBody().asByteArray();
		String filePath = "./src/test/resources/users.json";
//		File file = new File(filePath);
//		Files.write(file.toPath(), bytes);
		
		// write response to file
		PrintWriter pw = new PrintWriter(filePath);
		pw.write(response.asString());
		pw.flush();
		pw.close();
		System.out.println(response.asString());
		
		// read response from file
		FileInputStream fis = new FileInputStream(filePath);
		ObjectMapper om = new ObjectMapper();
		Map<?, ?> map = om.readValue(fis, Map.class);
		JSONObject json = new JSONObject(map);
		
		// validate respons
		assertThat("Response ID ", JsonPath.read(json.toString(), "data.[0].id"), is(1));
		List<String> actual = JsonPath.read(json.toString(), "$.data[*].first_name");
		System.out.println("Actual " + actual);
		List<String> expectedNames = List.of("George", "Janet");
		System.out.println("Expected " + expectedNames);
		assertThat("Response Array Data Missing", actual.containsAll(expectedNames));
	}
	
	@Test
	public void auth_1() throws Exception
	{
		Response response = RestAssured.given()
								.header("Authorization", "Bearer 11111111111111")
								.get("https://api.imgur.com/3/image/mjGvOUx")
								.thenReturn();
		System.out.println("Response ID "+ response.jsonPath().get("data.id"));
		System.out.println("Response link "+ response.jsonPath().get("data.link"));
	}
	
	@Test
	public void auth_2() throws Exception
	{
		Response response = RestAssured.given()
								.auth().oauth2("11111111111111")
								.get("https://api.imgur.com/3/image/mjGvOUx")
								.thenReturn();
		System.out.println("Response ID "+ response.jsonPath().get("data.id"));
		System.out.println("Response link "+ response.jsonPath().get("data.link"));
	}
}

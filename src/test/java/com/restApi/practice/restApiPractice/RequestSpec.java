package com.restApi.practice.restApiPractice;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

import java.util.List;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class RequestSpec {

	RequestSpecification reqSpec;
	ResponseSpecification resSpec;

	@BeforeTest
	public void preTest() {
		// step1
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.setBaseUri("https://reqres.in");
		reqBuilder.setBasePath("/api/users");
		// step2
		reqSpec = reqBuilder.build();
		// step3
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectContentType(ContentType.JSON);
		resBuilder.expectStatusCode(200);
		// step4
		resSpec = resBuilder.build();
	}

	@Test
	public void test1_noParam() {
		RestAssured
			.given()
			.spec(reqSpec)
			.when()
			.get()
			.then()
			.spec(resSpec);

	}
	
	@Test
	public void test1_withParam() {
		RestAssured
			.given()
			.spec(reqSpec)
			.queryParam("page", 2)
			.when()
			.get()
			.then()
			.spec(resSpec)
			.assertThat()
			.body("page", instanceOf(Integer.class))
			.body("page", notNullValue())
			.body("page", greaterThan(1))
			.body("data", hasSize(greaterThan(0)))
			.body("data", not(emptyArray()))
			.body("data.id", is(List.of(7, 8, 9, 10, 11, 12)))
			.body("data.first_name", hasItems(new String[]{"Michael", "Lindsay"}));//,
			//JsonSchemaValidator.matchesJsonSchemaInClasspath("schema.json"));
	}

}

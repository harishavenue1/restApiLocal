package com.restApi.practice.restApiPractice;

import static org.hamcrest.Matchers.*;
import java.util.List;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class P1 {
	public static void main(String[] args) {
		RestAssured
			.given()
			.baseUri("https://reqres.in/")
			.basePath("/api/users")
			.queryParam("page", 2)
			.log().all()
			.when()
			.get()
			.then()
			.log().all()
			.assertThat()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("page", instanceOf(Integer.class))
			.body("data", not(emptyArray()))
			.body("data.id", is(List.of(7,8,9,10,11,12)))
			.body("data.first_name", hasItems(new String[] {"Byron", "George"}));
	}
}

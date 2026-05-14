package com.example.apitests.books

import com.example.apitests.support.ApiClient
import com.example.apitests.support.TestConfig
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import static io.restassured.RestAssured.given

/**
 * Thin wrapper over the /Books REST endpoints.
 *
 * Each method maps one-to-one to a documented endpoint and returns the raw
 * {@link Response}. The wrapper deliberately does NOT assert on status codes
 * or response shapes - that's the spec's job. This separation lets one method
 * serve both happy-path tests (typed argument, sensible value) and
 * edge-case tests (Object id parameter accepts strings, decimals, etc.).
 */
final class BooksApi {

    private final RequestSpecification spec = ApiClient.jsonSpec()

    private final String booksPath = TestConfig.booksPath

    Response getAll() {
        given().spec(spec)
                .when().get(booksPath)
    }

    Response getById(Object id) {
        given().spec(spec)
                .pathParam("id", id)
                .when().get("${booksPath}/{id}")

    }

    Response create(Book book) {
        given().spec(spec).body(book)
                .when().post(booksPath)
    }

    Response create(Map<String, ?> rawBody) {
        given().spec(spec).body(rawBody)
                .when().post(booksPath)
    }

    Response createRaw(String rawBody) {
        given().spec(spec).body(rawBody)
                .when().post(booksPath)
    }

    Response update(Object id, Book book) {
        given().spec(spec)
                .pathParam("id", id).body(book)
                .when().put("${booksPath}/{id}")
    }

    Response update(Object id, Map<String, ?> rawBody) {
        given().spec(spec)
                .pathParam("id", id).body(rawBody)
                .when().put("${booksPath}/{id}")
    }

    Response delete(Object id) {
        given().spec(spec)
                .pathParam("id", id)
                .when().delete("${booksPath}/{id}")
    }
}

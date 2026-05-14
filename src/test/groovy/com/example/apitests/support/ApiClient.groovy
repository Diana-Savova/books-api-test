package com.example.apitests.support

import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.builder.RequestSpecBuilder

import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification

/**
 * Builds a pre-configured Rest-Assured {@link RequestSpecification} that the per-resource API wrappers BooksApi consume.
 *
 * Centralizing the spec keeps every HTTP concern in one place:
 *   - base URI (resolved from TestConfig, so env-var driven)
 *   - JSON content-type and accept headers
 *   - request/response logging (visible in test output)
 *   - Allure attachment of each call (visible in the HTML report)
 *
 * Swapping out the HTTP library or the reporting library means editing this one file rather than every test or every resource wrapper.
 */
final class ApiClient {

    private ApiClient() {} // not instantiable; pure builder
    static RequestSpecification jsonSpec() {
        new RequestSpecBuilder()
                .setBaseUri(TestConfig.baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build()
    }
}

package com.example.apitests.books.negative

import com.example.apitests.books.BooksApi
import com.example.apitests.support.HttpStatus
import com.example.apitests.support.ResponseAssertions
import com.example.apitests.support.TestIds

import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story

import io.restassured.response.Response

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Tag
import spock.lang.Title
import spock.lang.Unroll

@Epic("API test")
@Feature("Books API")
@Story("Negative - GET (single)")
@Title("GET /api/v1/Books/{id} - error and edge cases")
@Severity(SeverityLevel.NORMAL)
@Tag("negative")
class GetBookByIdNegativeSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()

    def "returns 404 for a non-existent (but well-formed numeric) id"() {

        when:
        Response response = books.getById(TestIds.unusedBookId())

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "returns 4xx for a far-out-of-range numeric id"() {

        when:
        Response response = books.getById(Integer.MAX_VALUE)

        then:
        HttpStatus.isClientError(response.statusCode)
    }

    @Unroll

    def "returns 4xx for malformed id '#id'"() {

        when:
        Response response = books.getById(id)

        then:
        HttpStatus.isClientError(response.statusCode)

        where:
        id << ["abc", "!!", " ", "-1", "1.5", "0.0", "null", "true", "%20"]
    }

    def "returns 4xx for id = 0 (sentinel value never assigned by the API)"() {

        when:
        Response response = books.getById(0)

        then:
        HttpStatus.isClientError(response.statusCode)
    }

    def "returns 4xx for a negative integer id"() {

        when:
        Response response = books.getById(-5)

        then:
        HttpStatus.isClientError(response.statusCode)
    }

    @Unroll

    def "never returns a 5xx for malformed id '#badId'"() {

        when:
        Response response = books.getById(badId)

        then:
        ResponseAssertions.noServerError(response)

        where:
        badId << ["abc", -1, 0, "1.5", " ", "null"]
    }
}

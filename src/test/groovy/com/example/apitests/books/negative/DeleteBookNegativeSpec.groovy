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
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Tag
import spock.lang.Title
import spock.lang.Unroll

@Epic("API test")
@Feature("Books API")
@Story("Negative - DELETE")
@Title("DELETE /api/v1/Books/{id} - error and edge cases")
@Severity(SeverityLevel.NORMAL)
@Tag("negative")
class DeleteBookNegativeSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()
    @Unroll
    def "returns 400 when id is non-numeric ('#badId')"() {

        when:
        Response response = books.delete(badId)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        where:
        badId << ["xyz", " ", "!!", "1.5", "null"]

    }
@Ignore
    def "returns 4xx for a negative id"() {

        when:
        Response response = books.delete(-1)

        then:
        HttpStatus.isClientError(response.statusCode)

    }

    def "is idempotent on a non-existent id - returns 200 not 404"() {

        when:
        Response response = books.delete(TestIds.unusedBookId())

        then:
        response.statusCode == HttpStatus.OK
    }

    @Unroll
    def "never returns a 5xx for malformed input '#badId'"() {

        when:
        Response response = books.delete(badId)

        then:
        ResponseAssertions.noServerError(response)

        where:
        badId << ["xyz", -1, "1.5", "null", " "]
    }
}

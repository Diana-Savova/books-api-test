package com.example.apitests.books.negative

import com.example.apitests.books.BookFixtures
import com.example.apitests.books.BooksApi
import com.example.apitests.support.HttpStatus
import com.example.apitests.support.ResponseAssertions

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

@Epic("API test")
@Feature("Books API")
@Story("Negative - POST (create)")
@Title("POST /api/v1/Books - error and edge cases")
@Severity(SeverityLevel.NORMAL)
@Tag("negative")
class CreateBookNegativeSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()

    // -----------------------------------------------------------------
    // Type violations - server should reject with 400
    // -----------------------------------------------------------------
    def "rejects with 400 when pageCount is a string"() {

        when:
        Response response = books.create(BookFixtures.aBookWithStringPageCount())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "rejects with 400 when id is a string"() {

        when:
        Response response = books.create(BookFixtures.aBookWithStringId())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "rejects with 400 when id is a decimal"() {

        when:
        Response response = books.create(BookFixtures.aBookWithDecimalId())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

    }

    def "rejects with 400 when publishDate is not a valid date"() {

        when:
        Response response = books.create(BookFixtures.aBookWithInvalidDate())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

    }

    // -----------------------------------------------------------------
    // Malformed request body - any 4xx is acceptable
    // -----------------------------------------------------------------
    def "rejects with 4xx when body is not in JSON format"() {

        when:
        Response response = books.createRaw(BookFixtures.anUnparseableBody())

        then:
        HttpStatus.isClientError(response.statusCode)

    }

    def "rejects with 4xx when body is malformed JSON (unquoted keys)"() {

        when:
        Response response = books.createRaw(BookFixtures.aMalformedJsonBody())

        then:
        HttpStatus.isClientError(response.statusCode)
    }

    def "rejects with 4xx when body is a JSON array instead of an object"() {

        when:
        Response response = books.createRaw(BookFixtures.aJsonArrayInsteadOfObject())

        then:
        HttpStatus.isClientError(response.statusCode)
    }

    // -----------------------------------------------------------------
    // Boundary / business-rule edges - response must be non-5xx, but
    // FakeRestAPI is lenient so we don't lock in the exact code.
    // -----------------------------------------------------------------
    def "handles a negative pageCount without a 5xx"() {

        when:
        Response response = books.create(BookFixtures.aBookWithNegativePageCount())

        then:
        ResponseAssertions.noServerError(response)
    }

    def "handles a zero pageCount without a 5xx"() {

        when:
        Response response = books.create(BookFixtures.aBookWithZeroPageCount())

        then:
        ResponseAssertions.noServerError(response)
    }

    def "handles a missing title without a 5xx"() {

        when:
        Response response = books.create(BookFixtures.aBookWithMissingTitle())

        then:
        ResponseAssertions.noServerError(response)
    }

    def "handles an empty-string title without a 5xx"() {

        when:
        Response response = books.create(BookFixtures.aBookWithEmptyTitle())

        then:
        ResponseAssertions.noServerError(response)
    }

    def "handles a missing publishDate without a 5xx"() {

        when:
        Response response = books.create(BookFixtures.aBookWithMissingPublishDate())

        then:
        ResponseAssertions.noServerError(response)

    }

    def "handles an empty JSON object without a 5xx"() {

        when:
        Response response = books.create([:])

        then:
        ResponseAssertions.noServerError(response)
    }
}

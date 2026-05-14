package com.example.apitests.books.negative

import com.example.apitests.books.Book
import com.example.apitests.books.BookFixtures
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
@Story("Negative - PUT (update)")
@Title("PUT /api/v1/Books/{id} - error and edge cases")
@Severity(SeverityLevel.NORMAL)
@Tag("negative")
@Tag("books")

class UpdateBookNegativeSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()
    @Unroll

    def "rejects with 400 when path id is non-numeric ('#badId')"() {

        given:
        Book book = BookFixtures.aValidBook()

        when:
        Response response = books.update(badId, book)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        where:
        badId << ["abc", " ", "!!", "1.5"]
    }

    def "rejects with 400 when body pageCount is a string"() {

        when:
        Response response = books.update(TestIds.validBookId(),
                BookFixtures.aBookWithStringPageCount())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "rejects with 400 when body id is a string"() {

        when:
        Response response = books.update(TestIds.validBookId(),
                BookFixtures.aBookWithStringId())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "rejects with 400 when body publishDate is malformed"() {

        when:
        Response response = books.update(TestIds.validBookId(),
                BookFixtures.aBookWithInvalidDate())

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "handles update with a negative pageCount without a 5xx"() {

        when:
        Response response = books.update(TestIds.validBookId(),
                BookFixtures.aBookWithNegativePageCount())

        then:
        ResponseAssertions.noServerError(response)
    }

    def "does not return 5xx when title is missing from update payload"() {

        when:
        Response response = books.update(TestIds.validBookId(),

                BookFixtures.aBookWithMissingTitle())

        then:
        ResponseAssertions.noServerError(response)
    }

    def "handles update with an empty JSON body without a 5xx"() {

        when:
        Response response = books.update(TestIds.validBookId(), [:])

        then:
        ResponseAssertions.noServerError(response)
    }
}

package com.example.apitests.books.happyPath

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
@Story("Happy path - GET (collection)")
@Title("GET /api/v1/Books - retrieve all books (happy path)")
@Severity(SeverityLevel.CRITICAL)
@Tag("happy-path")
@Tag("critical")
class GetAllBooksHappyPathSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()

    def "responds 200 OK with a JSON list of books"() {

        when:
        Response response = books.getAll()

        then:
        ResponseAssertions.isSuccessfulJson(response)
        !response.jsonPath().getList("").isEmpty()
    }

    def "every book in the list contains every documented field"() {

        when:
        Response response = books.getAll()

        then:
        response.statusCode == HttpStatus.OK
        ResponseAssertions.everyItemHasAllBookFields(response)
    }

    def "every book has the documented field types"() {

        when:
        Response response = books.getAll()

        then:
        ResponseAssertions.everyItemHasValidBookTypes(response)
    }

    def "id values are positive integers and unique across the collection"() {

        when:
        Response response = books.getAll()

        then:
        ResponseAssertions.everyItemHasPositiveId(response)
        ResponseAssertions.allIdsAreUnique(response)
    }

    def "publishDate matches an ISO-8601-like pattern for every book"() {

        when:
        Response response = books.getAll()

        then:
        ResponseAssertions.everyItemHasIsoPublishDate(response)
    }

    def "responds within an acceptable time budget"() {

        when:
        Response response = books.getAll()

        then:
        response.statusCode == HttpStatus.OK
        ResponseAssertions.isWithinTimeBudget(response)
    }
}

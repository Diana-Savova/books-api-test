package com.example.apitests.books.happyPath

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
@Story("Happy path - GET (single)")
@Title("GET /api/v1/Books/{id} - retrieve a single book (happy path)")
@Severity(SeverityLevel.CRITICAL)
@Tag("happy-path")
@Tag("critical")
class GetBookByIdHappyPathSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()

    def "returns a book whose id matches the path id"() {

        given:
        int id = TestIds.validBookId()

        when:
        Response response = books.getById(id)

        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("id") == id
    }

    @Unroll
    def "returns 200 with matching id for possible range of id #id"() {

        when:
        Response response = books.getById(id)

        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("id") == id

        where:
        id << [1, 50, 100, 150]
    }

    def "returned book contains every documented field"() {

        when:
        Response response = books.getById(TestIds.validBookId())

        then:
        response.statusCode == HttpStatus.OK
        ResponseAssertions.hasAllBookFields(response)
    }

    def "publishDate is in an ISO-8601-like format"() {

        when:
        Response response = books.getById(TestIds.validBookId())

        then:
        ResponseAssertions.hasIsoPublishDate(response)
    }

    def "Content-Type indicates JSON"() {

        when:
        Response response = books.getById(TestIds.validBookId())

        then:
        ResponseAssertions.isSuccessfulJson(response)
    }
}

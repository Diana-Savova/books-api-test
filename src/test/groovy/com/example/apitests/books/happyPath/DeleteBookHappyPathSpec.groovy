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
@Story("Happy path - DELETE")
@Title("DELETE /api/v1/Books/{id} - delete a book (happy path)")
@Severity(SeverityLevel.CRITICAL)
@Tag("happy-path")
@Tag("critical")
class DeleteBookHappyPathSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()

    def "deletes an existing book and returns 200"() {

        when:
        Response response = books.delete(TestIds.validBookId())

        then:
        response.statusCode == HttpStatus.OK
    }

    @Unroll
    def "DELETE book - id range"() {

        when:
        Response response = books.delete(id)

        then:
        response.statusCode == HttpStatus.OK

        where:
        id << [2, 5, 25, 50, 100]
    }

    def "deleting the same id twice still returns 200"() {

        given:
        int id = TestIds.validBookId()

        when:
        Response first  = books.delete(id)
        Response second = books.delete(id)

        then:
        first.statusCode  == HttpStatus.OK
        second.statusCode == HttpStatus.OK
    }

    def "responds within an acceptable time"() {

        when:
        Response response = books.delete(TestIds.validBookId())

        then:
        response.statusCode == HttpStatus.OK
        ResponseAssertions.isWithinTimeBudget(response)
    }
}

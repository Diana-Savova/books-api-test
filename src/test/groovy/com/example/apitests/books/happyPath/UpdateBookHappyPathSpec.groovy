package com.example.apitests.books.happyPath

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

@Epic("API test")
@Feature("Books API")
@Story("Happy path - PUT (update)")
@Title("PUT /api/v1/Books/{id} - update a book (happy path)")
@Severity(SeverityLevel.CRITICAL)
@Tag("happy-path")
@Tag("critical")
class UpdateBookHappyPathSpec extends Specification {
    @Shared @Subject BooksApi books = new BooksApi()
    def "updates an existing book and verify the new state back"() {
        given:
        int id = TestIds.validBookId()
        Book book = BookFixtures.aValidBook {
            delegate.id = id
            title       = "Updated title"
            description = "Updated description"
            pageCount   = 999
        }
        when:
        Response response = books.update(id, book)
        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getString("title")       == "Updated title"
        response.jsonPath().getString("description") == "Updated description"
        response.jsonPath().getInt("pageCount")      == 999
    }
    def "PUT to an id that doesn't exist still returns 200"() {
        given:
        int id = TestIds.unusedBookId()
        Book book = BookFixtures.aValidBookWithId(id)
        when:
        Response response = books.update(id, book)
        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("id") == id
    }
    def "verify the response of the updated books contains the id of the book"() {
        given:
        int id = TestIds.validBookId()
        Book book = BookFixtures.aValidBookWithId(id)
        when:
        Response response = books.update(id, book)
        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("id") == id
    }
    def "response Content-Type is JSON"() {
        given:
        int id = TestIds.validBookId()
        Book book = BookFixtures.aValidBookWithId(id)
        when:
        Response response = books.update(id, book)
        then:
        ResponseAssertions.isSuccessfulJson(response)
    }
}
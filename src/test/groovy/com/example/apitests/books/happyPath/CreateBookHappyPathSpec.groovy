package com.example.apitests.books.happyPath

import com.example.apitests.books.Book
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
@Story("Happy path - POST (create)")
@Title("POST /api/v1/Books - create a book (happy path)")
@Severity(SeverityLevel.CRITICAL)
@Tag("happy-path")
@Tag("critical")
class CreateBookHappyPathSpec extends Specification {

    @Shared @Subject BooksApi books = new BooksApi()

    def "creates a book and verify the field back"() {

        given:
        Book book = BookFixtures.aValidBook()

        when:
        Response response = books.create(book)

        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("id")             == book.id
        response.jsonPath().getString("title")       == book.title
        response.jsonPath().getString("description") == book.description
        response.jsonPath().getInt("pageCount")      == book.pageCount
        response.jsonPath().getString("excerpt")     == book.excerpt
        response.jsonPath().getString("publishDate") == book.publishDate
    }

    def "creates a book with less fields"() {

        given:
        Book book = BookFixtures.aValidBook {
            description = ""
            pageCount   = 1
            excerpt     = ""
        }

        when:
        Response response = books.create(book)

        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("pageCount") == 1
    }

    def "create a book with a very long title"() {

        given:
        Book book = BookFixtures.aValidBook {
            title = "x" * 5000
        }

        when:
        Response response = books.create(book)

        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getString("title").length() == 5000
    }

    def "create a book with a large pageCount"() {

        given:
        Book book = BookFixtures.aValidBook {
            pageCount = 100_000
        }

        when:
        Response response = books.create(book)

        then:
        response.statusCode == HttpStatus.OK
        response.jsonPath().getInt("pageCount") == 100_000
    }

    def "response is JSON and arrives within an acceptable time"() {

        given:
        Book book = BookFixtures.aValidBook()

        when:
        Response response = books.create(book)

        then:
        ResponseAssertions.isSuccessfulJson(response)
        ResponseAssertions.isWithinTimeBudget(response)
    }
}

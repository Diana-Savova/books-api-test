package com.example.apitests.support

import io.restassured.response.Response

import java.util.regex.Pattern

/**
 * Response-level assertion helpers that extract repeated patterns out of specs. Every method returns a boolean
 * so it can be used inside a Spock `then:` block as a single expression - and Spock's power-assert will
 * still show useful failure detail because the underlying assertions live here as plain Groovy expressions.
 *
 * Why static helpers and not a base spec class:
 *   - No inheritance hierarchy to maintain.
 *   - Composable - tests use only the helpers they need.
 *   - Easy to test in isolation if we ever want to.
 *
 * Naming convention: methods that return a boolean are named `isXxx` or
 * `hasXxx`; methods that compute a value have a noun name.
 */
final class ResponseAssertions {

    /** date/time pattern: yyyy-MM-ddTHH:mm:ss */
    static final Pattern ISO_DATE_PATTERN =
            ~/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.*/

    /** Fields every Book response must carry. */
    static final List<String> BOOK_FIELDS =
            ["id", "title", "description", "pageCount", "excerpt", "publishDate"].asImmutable()

    /** True if the response is JSON and the status is 2xx. */
    static boolean isSuccessfulJson(Response response) {
        HttpStatus.isSuccess(response.statusCode) && response.contentType.contains("json")
    }

    /** True if the response is *not* a server-side error (no 5xx leaked). */
    static boolean noServerError(Response response) {
        !HttpStatus.isServerError(response.statusCode)
    }

    /** True if a single-Book response carries every documented field. */
    static boolean hasAllBookFields(Response response) {
        Map body = response.jsonPath().getMap("")
        body.keySet().containsAll(BOOK_FIELDS)
    }

    /** True if every element of a collection response has every documented field. */
    static boolean everyItemHasAllBookFields(Response response) {
        response.jsonPath().getList("").every { Map item ->
            item.keySet().containsAll(BOOK_FIELDS)
        }
    }

    /** True if every element of a collection response has the documented field types. */
    static boolean everyItemHasValidBookTypes(Response response) {
        response.jsonPath().getList("").every { Map b ->
            b.id          instanceof Integer &&
                    b.title       instanceof String  &&
                    b.description instanceof String  &&
                    b.pageCount   instanceof Integer &&
                    b.excerpt     instanceof String  &&
                    b.publishDate instanceof String
        }
    }

    /** True if a single-Book response's publishDate matches the ISO date pattern. */
    static boolean hasIsoPublishDate(Response response) {
        response.jsonPath().getString("publishDate") ==~ ISO_DATE_PATTERN
    }

    /** True if every element in a collection response has an ISO-formatted publishDate. */
    static boolean everyItemHasIsoPublishDate(Response response) {
        response.jsonPath().getList("").every { Map it ->
            it.publishDate ==~ ISO_DATE_PATTERN
        }
    }

    /** True if every id in a collection response is a positive integer. */
    static boolean everyItemHasPositiveId(Response response) {
        response.jsonPath().getList("").every { Map it ->
            it.id instanceof Integer && it.id > 0
        }
    }

    /** True if every id in a collection response is unique. */
    static boolean allIdsAreUnique(Response response) {
        List<Map> items = response.jsonPath().getList("")
        items.collect { it.id }.toSet().size() == items.size()
    }

    /** True if response time is within the supplied budget (milliseconds). */
    static boolean isWithinTimeBudget(Response response, long budgetMs = 10_000) {
        response.time < budgetMs
    }

    private ResponseAssertions() {}
}

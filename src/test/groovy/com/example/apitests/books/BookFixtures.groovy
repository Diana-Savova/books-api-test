package com.example.apitests.books

import com.example.apitests.support.TestIds

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Test fixtures for Book payloads.
 *
 * The file has two halves with different design philosophies on purpose:
 *
 *   Happy-path side - prefers a builder + random defaults.
 *     Most happy-path tests don't care about specific field values; they
 *     care that "a valid book" round-trips. Hard-coding values across many
 *     tests creates the false impression of shared state. The builder
 *     supplies sensible randomized defaults; tests override only the
 *     fields they care about:
 *
 *         Book book = aValidBook()                       // all defaults
 *         Book book = aValidBook { title = "Foo" }        // override one
 *
 *   Negative side - named-scenario factory methods.
 *     Negative fixtures are not "a random invalid book"; each represents
 *     a SPECIFIC intent (missing title, malformed date, string in an
 *     integer field). Naming each method makes specs read as
 *     documentation: `aBookWithMissingTitle()`, not
 *     `BookBuilder.broken().missing("title")`.
 */
final class BookFixtures {
    private static final DateTimeFormatter ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneOffset.UTC)

    // ---------------------------------------------------------------------
    // Happy-path side: builder with sensible randomized defaults
    // ---------------------------------------------------------------------

    /**
     * A valid Book with randomized defaults. Pass a Closure to override
     * specific fields:
     *
     *   def book = BookFixtures.aValidBook { pageCount = 999 }
     *
     * The id defaults to a fresh random value so two tests running in
     * parallel never accidentally clash on the same payload.
     */
    static Book aValidBook(@DelegatesTo(Book) Closure overrides = null) {
        Book book = new Book(
                id:          TestIds.newBookId(),
                title:       "Test Book ${System.nanoTime()}",
                description: "Auto-generated description",
                pageCount:   200,
                excerpt:     "An excerpt of the book",
                publishDate: ISO.format(Instant.now())
        )
        if (overrides != null) {
            overrides.delegate = book
            overrides.resolveStrategy = Closure.DELEGATE_FIRST
            overrides.call()
        }
        book
    }

    /** Convenience: a valid book with a specific id (when the test cares). */
    static Book aValidBookWithId(int id) {
        aValidBook { delegate.id = id }
    }

    // ---------------------------------------------------------------------
    // Negative side: named scenarios for malformed payloads
    //
    // These return raw Maps because some bad payloads can't pass through
    // the typed Book model (e.g. a string in an Integer field, or an
    // omitted field with a primitive-typed counterpart).
    // ---------------------------------------------------------------------

    /** Type violation: pageCount must be an integer; sending a string. */
    static Map<String, ?> aBookWithStringPageCount() {
        validJsonShape().tap {
            it.pageCount = "many"
        }
    }
    /** Type violation: id must be an integer; sending a string. */
    static Map<String, ?> aBookWithStringId() {
        validJsonShape().tap {
            it.id = "abc"
        }
    }
    /** Type violation: id is a floating point number. */
    static Map<String, ?> aBookWithDecimalId() {
        validJsonShape().tap {
            it.id = 1.5
        }
    }
    /** Format violation: publishDate must be ISO-8601-like; sending free text. */
    static Map<String, ?> aBookWithInvalidDate() {
        validJsonShape().tap {
            it.publishDate = "yesterday"
        }
    }
    /** Boundary violation: negative page count is nonsensical for a real book. */
    static Map<String, ?> aBookWithNegativePageCount() {
        validJsonShape().tap {
            it.pageCount = -10
        }
    }
    /** Boundary violation: zero pages. */
    static Map<String, ?> aBookWithZeroPageCount() {
        validJsonShape().tap {
            it.pageCount = 0
        }
    }
    /** Missing required field: title omitted. */
    static Map<String, ?> aBookWithMissingTitle() {
        validJsonShape().tap {
            it.remove("title")
        }
    }
    /** Edge: title present but empty string. */
    static Map<String, ?> aBookWithEmptyTitle() {
        validJsonShape().tap {
            it.title = ""
        }
    }
    /** Missing required field: publishDate omitted. */
    static Map<String, ?> aBookWithMissingPublishDate() {
        validJsonShape().tap {
            it.remove("publishDate")
        }
    }
    // Raw-string bodies - things Jackson cannot serialize for us.
    /** Not JSON at all - plain text. */
    static String anUnparseableBody() {
        "this is not json"
    }
    /** Looks like JSON but is malformed (unquoted keys, single quotes). */
    static String aMalformedJsonBody() {
        "{ id: 1, title: 'Single quoted strings are not valid JSON' }"
    }
    /** JSON array where the API expects a JSON object. */
    static String aJsonArrayInsteadOfObject() {
        "[1, 2, 3]"
    }
    // ---------------------------------------------------------------------
    // Internal: produces a valid Map shape that negative fixtures mutate.
    // Kept private because tests should reach for a named scenario, not
    // build their own from this base.
    // ---------------------------------------------------------------------
    private static Map<String, ?> validJsonShape() {
        [id:          TestIds.newBookId(),
         title:       "Test Book ${System.nanoTime()}",
         description: "Auto-generated description",
         pageCount:   100,
         excerpt:     "An excerpt",
         publishDate: ISO.format(Instant.now())]
    }
    private BookFixtures() {}
}
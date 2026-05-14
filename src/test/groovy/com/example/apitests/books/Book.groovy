package com.example.apitests.books

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Book domain object matching the FakeRestAPI Book schema.
 *
 * Example JSON:
 *   { "id": 1, "title": "Book 1",
 *     "description": "...", "pageCount": 100,
 *     "excerpt": "...", "publishDate": "2025-01-15T12:00:00" }
 */
@ToString(includePackage = false, includeNames = true)
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
class Book {
    Integer id
    String  title
    String  description
    Integer pageCount
    String  excerpt
    String  publishDate
}
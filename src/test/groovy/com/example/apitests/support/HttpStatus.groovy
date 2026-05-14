package com.example.apitests.support

/**
 * Named HTTP status code constants and tiny range helpers.
 * Tests read as:
 *   response.statusCode == HttpStatus.OK
 *   HttpStatus.isClientError(response.statusCode)
 */
final class HttpStatus {
    static final int OK                    = 200
    static final int BAD_REQUEST           = 400
    static final int NOT_FOUND             = 404

    /** True if status indicates a successful request (2xx). */
    static boolean isSuccess(int status) {
        status >= 200 && status < 300
    }

    /** True if status indicates a client-side error (4xx). */
    static boolean isClientError(int status) {
        status >= 400 && status < 500
    }

    /** True if status indicates a server-side error (5xx). */
    static boolean isServerError(int status) {
        status >= 500 && status < 600
    }

    private HttpStatus() {} // not instantiable
}

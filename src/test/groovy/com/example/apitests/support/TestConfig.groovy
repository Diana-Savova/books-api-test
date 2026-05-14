package com.example.apitests.support

/**
 * Single source of truth for runtime configuration.
 *
 * Resolution order:
 *   1. System property (e.g. -Dapi.base.url=...)
 *   2. Environment variable (e.g. API_BASE_URL)
 *   3. Hard-coded default
 *
 * The same compiled artifact runs locally, in an IDE, under Maven, and inside
 * a Docker container without code changes - configuration always flows from
 * the outside in.
 */
final class TestConfig {

    private static final String DEFAULT_BASE_URL  = "https://fakerestapi.azurewebsites.net"
    private static final String DEFAULT_BASE_PATH = "/api/v1"

    static String getBaseUrl() {
        resolve("api.base.url", "API_BASE_URL", DEFAULT_BASE_URL)

    }

    static String getBasePath() {
        resolve("api.basePath", "API_BASE_PATH", DEFAULT_BASE_PATH)
    }

    static String getBooksPath() { "${basePath}/Books" }

    private static String resolve(String sysProp, String envVar, String fallback) {

        String fromSystem = System.getProperty(sysProp)
        if (fromSystem?.trim()) return fromSystem.trim()
        String fromEnv = System.getenv(envVar)
        if (fromEnv?.trim()) return fromEnv.trim()
        fallback
    }

    private TestConfig() {} // not instantiable
}

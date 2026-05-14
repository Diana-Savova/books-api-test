package com.example.apitests.support

import java.util.concurrent.ThreadLocalRandom

/**
 * Random ID generator for happy-path tests where the specific value is
 * irrelevant (we just need "some valid id").
 *
 * Why a helper instead of inlining ThreadLocalRandom.current().nextInt(...):
 *   1. Tests document intent: validId() vs newId() vs unusedId() each mean a different thing.
 *   2. The ranges are tunable in one place if FakeRestAPI's seeded data changes.
 *   3. Parameterized data tables (where: blocks) should NOT use these -
 *      they need reproducible inputs. This helper is only for tests where
 *      the value is incidental.
 */

final class TestIds {
    // FakeRestAPI seeds /Books with ids 1..200 (approx). These ranges
    // are conservative within that.

    private static final int SEEDED_MIN     = 1
    private static final int SEEDED_MAX     = 150
    private static final int UNUSED_MIN     = 10_000
    private static final int UNUSED_MAX     = 99_999

    /** A random id that should resolve to a seeded book (GET should return 200). */
    static int validBookId() {
        ThreadLocalRandom.current().nextInt(SEEDED_MIN, SEEDED_MAX + 1)
    }

    /** A random id well outside the seeded range (GET should return 404). */
    static int unusedBookId() {
        ThreadLocalRandom.current().nextInt(UNUSED_MIN, UNUSED_MAX + 1)
    }

    /** A random id suitable for POST payloads (server doesn't persist, so collisions are fine). */
    static int newBookId() {
        ThreadLocalRandom.current().nextInt(1_000, 9_999 + 1)
    }

    private TestIds() {}
}

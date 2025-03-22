@file:Suppress("ktlint:standard:no-wildcard-imports")

import kotlin.test.Test
import kotlin.test.assertEquals

class CacheTest {
    private val text = "Hello, test!"

    @Test
    fun testCacheSetAndGet() {
        var cache = Cache<String>()[CacheKey.SUMMARY_TEXT]
        assertEquals(cache, null)

        cache = text
        assertEquals(text, cache)
    }
}

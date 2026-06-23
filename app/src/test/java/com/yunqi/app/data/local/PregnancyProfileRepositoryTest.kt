package com.yunqi.app.data.local

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PregnancyProfileRepositoryTest {
    @Test
    fun `parses stored local dates safely`() {
        assertEquals(
            LocalDate.of(2026, 6, 21),
            "2026-06-21".parseStoredLocalDateOrNull(),
        )
        assertEquals(
            LocalDate.of(2026, 6, 21),
            " 2026-06-21 ".parseStoredLocalDateOrNull(),
        )
    }

    @Test
    fun `returns null for blank or corrupt stored dates`() {
        assertNull(null.parseStoredLocalDateOrNull())
        assertNull("   ".parseStoredLocalDateOrNull())
        assertNull("2026/06/21".parseStoredLocalDateOrNull())
    }
}

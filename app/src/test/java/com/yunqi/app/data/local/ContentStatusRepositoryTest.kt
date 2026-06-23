package com.yunqi.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ContentStatusRepositoryTest {
    @Test
    fun `toggle content id adds normalized id`() {
        val contentIds = emptySet<String>().toggleContentId("  diet_first_trimester_folate  ")

        assertEquals(setOf("diet_first_trimester_folate"), contentIds)
    }

    @Test
    fun `toggle content id removes existing normalized id`() {
        val contentIds = setOf("diet_first_trimester_folate")
            .toggleContentId(" diet_first_trimester_folate ")

        assertEquals(emptySet<String>(), contentIds)
    }

    @Test
    fun `toggle content id ignores blank id`() {
        val existingContentIds = setOf("exercise_first_trimester_walk")

        val contentIds = existingContentIds.toggleContentId("   ")

        assertEquals(existingContentIds, contentIds)
    }
}

package com.yunqi.app.data.export

import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRecordExportStoreTest {
    private val store = CalendarRecordExportStore()

    @Test
    fun `writes csv export into exports cache directory`() {
        val cacheDir = Files.createTempDirectory("yunqi-export-cache").toFile()

        val file = store.write(cacheDir, "id,date\n")

        assertEquals("yunqi-calendar-records.csv", file.name)
        assertEquals("exports", file.parentFile?.name)
        assertEquals("id,date\n", file.readText(Charsets.UTF_8))
    }

    @Test
    fun `clears cached csv exports`() {
        val cacheDir = Files.createTempDirectory("yunqi-export-cache").toFile()
        val file = store.write(cacheDir, "id,date\n")
        val exportsDir = requireNotNull(file.parentFile)

        store.clear(cacheDir)

        assertFalse(file.exists())
        assertFalse(exportsDir.exists())
        assertTrue(cacheDir.exists())
    }
}

package com.yunqi.app.data.export

import java.io.File

class CalendarRecordExportStore(
    private val exportFileName: String = "yunqi-calendar-records.csv",
) {
    /**
     * Writes the latest CSV export into the app cache so it can be shared with a FileProvider.
     */
    fun write(cacheDir: File, csv: String): File {
        val exportsDir = cacheDir.exportsDir().apply { mkdirs() }
        return File(exportsDir, exportFileName).apply {
            writeText(csv, Charsets.UTF_8)
        }
    }

    /**
     * Removes cached CSV exports when the user clears all local pregnancy data.
     */
    fun clear(cacheDir: File) {
        cacheDir.exportsDir().deleteRecursively()
    }

    private fun File.exportsDir(): File = File(this, EXPORTS_DIR_NAME)

    private companion object {
        const val EXPORTS_DIR_NAME = "exports"
    }
}

package com.yunqi.app.feature.common

import androidx.compose.ui.text.input.KeyboardType
import org.junit.Assert.assertEquals
import org.junit.Test

class IsoDateInputTest {
    @Test
    fun `iso date keyboard supports hyphen entry`() {
        assertEquals(KeyboardType.Ascii, isoDateKeyboardOptions().keyboardType)
    }
}

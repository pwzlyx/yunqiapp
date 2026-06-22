package com.yunqi.app.feature.common

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

/**
 * Provides a keyboard that can enter ISO dates such as YYYY-MM-DD, including hyphens.
 */
internal fun isoDateKeyboardOptions(): KeyboardOptions =
    KeyboardOptions(keyboardType = KeyboardType.Ascii)

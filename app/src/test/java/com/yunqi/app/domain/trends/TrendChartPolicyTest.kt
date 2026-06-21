package com.yunqi.app.domain.trends

import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrendChartPolicyTest {
    @Test
    fun `does not show chart with fewer than two points`() {
        assertFalse(TrendChartPolicy.shouldShowChart(emptyList()))
        assertFalse(
            TrendChartPolicy.shouldShowChart(
                listOf(TrendPoint(LocalDate.of(2026, 6, 21), 56.0)),
            ),
        )
    }

    @Test
    fun `shows chart when at least two points can be compared`() {
        assertTrue(
            TrendChartPolicy.shouldShowChart(
                listOf(
                    TrendPoint(LocalDate.of(2026, 6, 20), 55.0),
                    TrendPoint(LocalDate.of(2026, 6, 21), 56.0),
                ),
            ),
        )
    }
}

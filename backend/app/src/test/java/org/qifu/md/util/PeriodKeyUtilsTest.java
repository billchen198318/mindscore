package org.qifu.md.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PeriodKeyUtilsTest {
    @Test
    void parsesAndFormatsIsoWeekAcrossCalendarYear() throws Exception {
        LocalDate start = PeriodKeyUtils.parseStart("WEEK", "2020-W53");
        assertEquals(LocalDate.of(2020, 12, 28), start);
        assertEquals("2020-W53", PeriodKeyUtils.format("WEEK", LocalDate.of(2021, 1, 1)));
    }

    @Test
    void rejectsNonexistentIsoWeekAndInvalidDate() {
        assertFalse(PeriodKeyUtils.isValid("WEEK", "2021-W53"));
        assertFalse(PeriodKeyUtils.isValid("DAY", "2025-02-29"));
        assertTrue(PeriodKeyUtils.isValid("DAY", "2024-02-29"));
    }

    @Test
    void calculatesPeriodEnd() throws Exception {
        assertEquals(LocalDate.of(2025, 3, 31), PeriodKeyUtils.end("QUARTER", "2025-Q1"));
        assertEquals(LocalDate.of(2025, 12, 31), PeriodKeyUtils.end("HALFYEAR", "2025-H2"));
    }
}

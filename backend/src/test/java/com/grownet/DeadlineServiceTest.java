
package com.grownet;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DeadlineServiceTest {

    private final DeadlineService service = new DeadlineService();

    private final LocalDate today = LocalDate.of(2026, 10, 17);

    @Test
    void categorizesPastDeadlineAsExpired() {
        assertEquals(
                DeadlineService.DeadlineStatus.EXPIRED,
                service.categorize(today.minusDays(1), today)
        );
    }

    @Test
    void categorizesTodayAsClosingSoon() {
        assertEquals(
                DeadlineService.DeadlineStatus.CLOSING_SOON,
                service.categorize(today, today)
        );
    }

    @Test
    void categorizesDeadlineSevenDaysAwayAsClosingSoon() {
        assertEquals(
                DeadlineService.DeadlineStatus.CLOSING_SOON,
                service.categorize(today.plusDays(7), today)
        );
    }

    @Test
    void categorizesDeadlineEightDaysAwayAsUpcoming() {
        assertEquals(
                DeadlineService.DeadlineStatus.UPCOMING,
                service.categorize(today.plusDays(8), today)
        );
    }

    @Test
    void categorizesNullDeadlineAsNoDeadline() {
        assertEquals(
                DeadlineService.DeadlineStatus.NO_DEADLINE,
                service.categorize(null, today)
        );
    }

    @Test
    void rejectsNullToday() {
        assertThrows(
                NullPointerException.class,
                () -> service.categorize(today, null)
        );
    }

    @Test
    void categorizesDeadlineOnTheLastDayOfClosingSoonWindow() {
        LocalDate deadline = today.plusDays(7);

        assertEquals(
                DeadlineService.DeadlineStatus.CLOSING_SOON,
                service.categorize(deadline, today)
        );
    }

    @Test
    void categorizesDeadlineBeyondClosingSoonWindowAsUpcoming() {
        LocalDate deadline = today.plusDays(30);

        assertEquals(
                DeadlineService.DeadlineStatus.UPCOMING,
                service.categorize(deadline, today)
        );
    }
}

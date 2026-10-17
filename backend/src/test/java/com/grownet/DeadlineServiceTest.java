package com.grownet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineServiceTest {

    private final DeadlineService service = new DeadlineService();
    private final LocalDate today = LocalDate.of(2026, 10, 16);

    @Test
    void returnsNoDeadlineWhenDateIsMissing() {
        assertEquals(DeadlineService.DeadlineStatus.NO_DEADLINE,
                service.categorize(null, today));
    }

    @Test
    void returnsExpiredForPastDate() {
        assertEquals(DeadlineService.DeadlineStatus.EXPIRED,
                service.categorize(today.minusDays(1), today));
    }

    @Test
    void returnsClosingSoonForToday() {
        assertEquals(DeadlineService.DeadlineStatus.CLOSING_SOON,
                service.categorize(today, today));
    }

    @Test
    void returnsClosingSoonForSevenDaysAway() {
        assertEquals(DeadlineService.DeadlineStatus.CLOSING_SOON,
                service.categorize(today.plusDays(7), today));
    }

    @Test
    void returnsUpcomingForMoreThanSevenDaysAway() {
        assertEquals(DeadlineService.DeadlineStatus.UPCOMING,
                service.categorize(today.plusDays(8), today));
    }
}
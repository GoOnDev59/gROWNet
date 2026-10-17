package com.grownet;

import java.time.LocalDate;
import java.util.Objects;

public class DeadlineService {

    private static final long CLOSING_SOON_DAYS = 7;

    public enum DeadlineStatus {
        EXPIRED,
        CLOSING_SOON,
        UPCOMING,
        NO_DEADLINE
    }

    public DeadlineStatus categorize(LocalDate deadline, LocalDate today) {
        Objects.requireNonNull(today, "today must not be null");

        if (deadline == null) {
            return DeadlineStatus.NO_DEADLINE;
        }

        if (deadline.isBefore(today)) {
            return DeadlineStatus.EXPIRED;
        }

        if (!deadline.isAfter(today.plusDays(CLOSING_SOON_DAYS))) {
            return DeadlineStatus.CLOSING_SOON;
        }

        return DeadlineStatus.UPCOMING;
    }

    public DeadlineStatus categorize(LocalDate deadline) {
        return categorize(deadline, LocalDate.now());
    }
}

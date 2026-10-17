package com.grownet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class ReadinessServiceTest {

    private final ReadinessService service = new ReadinessService();

    @Test
    void calculatesReadinessPercentage() {
        var result = service.calculate(
                List.of(true, true, true, true, true, true, false, false)
        );

        assertEquals(75, result.score());
        assertEquals(6, result.completedItems());
        assertEquals(8, result.totalItems());
    }

    @Test
    void returnsZeroWhenNothingIsCompleted() {
        var result = service.calculate(List.of(false, false, false));

        assertEquals(0, result.score());
        assertEquals(0, result.completedItems());
        assertEquals(3, result.totalItems());
    }

    @Test
    void returnsOneHundredWhenEverythingIsCompleted() {
        var result = service.calculate(List.of(true, true));

        assertEquals(100, result.score());
    }

    @Test
    void handlesEmptyChecklist() {
        var result = service.calculate(List.of());

        assertEquals(0, result.score());
        assertEquals(0, result.totalItems());
    }

    @Test
    void handlesNullChecklist() {
        var result = service.calculate(null);

        assertEquals(0, result.score());
        assertEquals(0, result.totalItems());
    }
}
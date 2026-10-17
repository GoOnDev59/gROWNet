
package com.grownet;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadinessServiceTest {

    private final ReadinessService service = new ReadinessService();

    @Test
    void calculateReturns100WhenAllItemsAreCompleted() {
        ReadinessService.ReadinessResult result =
                service.calculate(List.of(true, true, true));

        assertEquals(100, result.score());
        assertEquals(3, result.completedItems());
        assertEquals(3, result.totalItems());
        assertEquals(
                "Completed 3 of 3 applicable checklist items.",
                result.explanation()
        );
    }

    @Test
    void calculateReturnsZeroWhenNoItemsAreCompleted() {
        ReadinessService.ReadinessResult result =
                service.calculate(List.of(false, false, false));

        assertEquals(0, result.score());
        assertEquals(0, result.completedItems());
        assertEquals(3, result.totalItems());
    }

    @Test
    void calculateReturnsCorrectPartialScore() {
        ReadinessService.ReadinessResult result =
                service.calculate(List.of(true, true, false, false));

        assertEquals(50, result.score());
        assertEquals(2, result.completedItems());
        assertEquals(4, result.totalItems());
    }

    @Test
    void calculateHandlesNullChecklist() {
        ReadinessService.ReadinessResult result =
                service.calculate(null);

        assertEquals(0, result.score());
        assertEquals(0, result.completedItems());
        assertEquals(0, result.totalItems());
        assertEquals(
                "No checklist items are available yet.",
                result.explanation()
        );
    }

    @Test
    void calculateHandlesEmptyChecklist() {
        ReadinessService.ReadinessResult result =
                service.calculate(List.of());

        assertEquals(0, result.score());
        assertEquals(0, result.completedItems());
        assertEquals(0, result.totalItems());
        assertEquals(
                "No checklist items are available yet.",
                result.explanation()
        );
    }

    @Test
    void calculateIgnoresNullItems() {
        ReadinessService.ReadinessResult result =
                service.calculate(Arrays.asList(true, null, false, true));

        assertEquals(67, result.score());
        assertEquals(2, result.completedItems());
        assertEquals(3, result.totalItems());
    }

    @Test
    void calculateHandlesChecklistContainingOnlyNullItems() {
        ReadinessService.ReadinessResult result =
                service.calculate(Arrays.asList(null, null));

        assertEquals(0, result.score());
        assertEquals(0, result.completedItems());
        assertEquals(0, result.totalItems());
        assertEquals(
                "No applicable checklist items are available.",
                result.explanation()
        );
    }

    @Test
    void calculateRoundsScoreToNearestInteger() {
        ReadinessService.ReadinessResult result =
                service.calculate(List.of(true, false, false));

        assertEquals(33, result.score());
        assertEquals(1, result.completedItems());
        assertEquals(3, result.totalItems());
    }
}

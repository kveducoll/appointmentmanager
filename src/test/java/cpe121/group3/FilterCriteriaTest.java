package cpe121.group3;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FilterCriteriaTest {

    @Test
    void defaultHasNoActiveFilters() {
        FilterCriteria fc = new FilterCriteria();
        assertFalse(fc.hasActiveFilters());
    }

    @Test
    void enablingFiltersChangesHasActiveFilters() {
        FilterCriteria fc = new FilterCriteria();
        fc.setTitleFilterEnabled(true);
        assertTrue(fc.hasActiveFilters());

        fc = new FilterCriteria();
        fc.setParticipantFilterEnabled(true);
        assertTrue(fc.hasActiveFilters());

        fc = new FilterCriteria();
        fc.setDateRangeFilterEnabled(true);
        fc.setFromDate(LocalDate.now());
        fc.setToDate(LocalDate.now().plusDays(1));
        assertTrue(fc.hasActiveFilters());
    }

    @Test
    void gettersAndSettersWork() {
        FilterCriteria fc = new FilterCriteria();
        fc.setTitleFilter("Meet");
        fc.setParticipantFilter("Alice");
        fc.setCaseSensitive(true);
        fc.setExactMatch(true);

        assertEquals("Meet", fc.getTitleFilter());
        assertEquals("Alice", fc.getParticipantFilter());
        assertTrue(fc.isCaseSensitive());
        assertTrue(fc.isExactMatch());
    }
}

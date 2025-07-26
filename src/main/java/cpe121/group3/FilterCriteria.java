package cpe121.group3;

import java.time.LocalDate;

// Criteria handelr
public class FilterCriteria {
    private boolean titleFilterEnabled;
    private String titleFilter;
    private boolean participantFilterEnabled;
    private String participantFilter;
    private boolean dateRangeFilterEnabled;
    private LocalDate fromDate;
    private LocalDate toDate;
    private boolean statusFilterEnabled;
    private String statusFilter;
    private boolean descriptionFilterEnabled;
    private String descriptionFilter;
    private boolean caseSensitive;
    private boolean exactMatch;

    // Constructor
    public FilterCriteria() {
        this.titleFilterEnabled = false;
        this.participantFilterEnabled = false;
        this.dateRangeFilterEnabled = false;
        this.statusFilterEnabled = false;
        this.descriptionFilterEnabled = false;
        this.caseSensitive = false;
        this.exactMatch = false;
    }

    // Getters and Setters
    public boolean isTitleFilterEnabled() {
        return titleFilterEnabled;
    }

    public void setTitleFilterEnabled(boolean titleFilterEnabled) {
        this.titleFilterEnabled = titleFilterEnabled;
    }

    public String getTitleFilter() {
        return titleFilter;
    }

    public void setTitleFilter(String titleFilter) {
        this.titleFilter = titleFilter;
    }

    public boolean isParticipantFilterEnabled() {
        return participantFilterEnabled;
    }

    public void setParticipantFilterEnabled(boolean participantFilterEnabled) {
        this.participantFilterEnabled = participantFilterEnabled;
    }

    public String getParticipantFilter() {
        return participantFilter;
    }

    public void setParticipantFilter(String participantFilter) {
        this.participantFilter = participantFilter;
    }

    public boolean isDateRangeFilterEnabled() {
        return dateRangeFilterEnabled;
    }

    public void setDateRangeFilterEnabled(boolean dateRangeFilterEnabled) {
        this.dateRangeFilterEnabled = dateRangeFilterEnabled;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public boolean isStatusFilterEnabled() {
        return statusFilterEnabled;
    }

    public void setStatusFilterEnabled(boolean statusFilterEnabled) {
        this.statusFilterEnabled = statusFilterEnabled;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    public boolean isDescriptionFilterEnabled() {
        return descriptionFilterEnabled;
    }

    public void setDescriptionFilterEnabled(boolean descriptionFilterEnabled) {
        this.descriptionFilterEnabled = descriptionFilterEnabled;
    }

    public String getDescriptionFilter() {
        return descriptionFilter;
    }

    public void setDescriptionFilter(String descriptionFilter) {
        this.descriptionFilter = descriptionFilter;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    // check if there's active filters
    public boolean hasActiveFilters() {
        return titleFilterEnabled || participantFilterEnabled || dateRangeFilterEnabled || 
               statusFilterEnabled || descriptionFilterEnabled;
    }
}

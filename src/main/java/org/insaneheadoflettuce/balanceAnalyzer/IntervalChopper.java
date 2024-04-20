package org.insaneheadoflettuce.balanceAnalyzer;

import org.springframework.data.util.Pair;

import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.*;

public class IntervalChopper {

    @FunctionalInterface
    public interface IntervalConsumer {
        void consume(LocalDate begin, LocalDate end);
    }

    private final LocalDate begin;
    private final LocalDate end;

    public IntervalChopper(Pair<LocalDate, LocalDate> interval) {
        this(interval.getFirst(), interval.getSecond());
    }

    public IntervalChopper(LocalDate begin, LocalDate end) {
        if (end.isBefore(begin)) {
            throw new IllegalArgumentException("End date cannot be before begin");
        }
        this.begin = begin;
        this.end = end;
    }

    public IntervalChopper walkMonths(IntervalConsumer consumer) {
        final var b = begin.with(firstDayOfMonth());
        final var e = end.with(lastDayOfMonth());
        for (LocalDate currentMonth = b; currentMonth.isBefore(e); currentMonth = currentMonth.plusMonths(1)) {
            consumer.consume(currentMonth, currentMonth.with(lastDayOfMonth()));
        }
        return this;
    }

    public IntervalChopper walkMonthsBack(IntervalConsumer consumer) {
        final var b = begin.with(firstDayOfMonth());
        final var e = end.with(lastDayOfMonth());
        for (LocalDate currentMonth = e; currentMonth.isAfter(b); currentMonth = currentMonth.minusMonths(1)) {
            consumer.consume(currentMonth.with(firstDayOfMonth()), currentMonth.with(lastDayOfMonth())); // lastDayOfMonth is required for end, because end of month changes
        }
        return this;
    }

    public IntervalChopper walkYears(IntervalConsumer consumer) {
        final var b = begin.with(firstDayOfYear());
        final var e = end.with(lastDayOfYear());
        for (LocalDate currentYear = b; currentYear.isBefore(e); currentYear = currentYear.plusYears(1)) {
            consumer.consume(currentYear, currentYear.with(lastDayOfYear()));
        }
        return this;
    }

    public IntervalChopper walkYearsBack(IntervalConsumer consumer) {
        final var b = begin.with(firstDayOfYear());
        final var e = end.with(lastDayOfYear());
        for (LocalDate currentYear = e; currentYear.isAfter(b); currentYear = currentYear.minusYears(1)) {
            consumer.consume(currentYear.with(firstDayOfYear()), currentYear);
        }
        return this;
    }
}

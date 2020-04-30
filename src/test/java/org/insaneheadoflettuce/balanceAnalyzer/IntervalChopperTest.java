package org.insaneheadoflettuce.balanceAnalyzer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class IntervalChopperTest
{
    @Test
    void walkMonths()
    {
        final var intervalChopper = new IntervalChopper(Pair.of(
                LocalDate.of(2019, 2, 23),
                LocalDate.of(2019, 10, 13)));

        List<LocalDate> beginMonths = new ArrayList<>();
        List<LocalDate> endMonths = new ArrayList<>();
        intervalChopper.walkMonths((begin, end) ->
        {
            beginMonths.add(begin);
            endMonths.add(end);
        });
        Assertions.assertEquals(9, beginMonths.size());
        Assertions.assertEquals(9, endMonths.size());

        Assertions.assertEquals(2019, beginMonths.get(0).getYear());
        Assertions.assertEquals(Month.FEBRUARY, beginMonths.get(0).getMonth());
        Assertions.assertEquals(1, beginMonths.get(0).getDayOfMonth());
        Assertions.assertEquals(2019, endMonths.get(0).getYear());
        Assertions.assertEquals(Month.FEBRUARY, endMonths.get(0).getMonth());
        Assertions.assertEquals(28, endMonths.get(0).getDayOfMonth());

        Assertions.assertEquals(2019, beginMonths.get(8).getYear());
        Assertions.assertEquals(Month.OCTOBER, beginMonths.get(8).getMonth());
        Assertions.assertEquals(1, beginMonths.get(8).getDayOfMonth());
        Assertions.assertEquals(2019, endMonths.get(8).getYear());
        Assertions.assertEquals(Month.OCTOBER, endMonths.get(8).getMonth());
        Assertions.assertEquals(31, endMonths.get(8).getDayOfMonth());
    }

    @Test
    void walkMonthsBack()
    {
        final var intervalChopper = new IntervalChopper(Pair.of(
                LocalDate.of(2019, 2, 23),
                LocalDate.of(2019, 10, 13)));

        List<LocalDate> beginMonths = new ArrayList<>();
        List<LocalDate> endMonths = new ArrayList<>();
        intervalChopper.walkMonthsBack((begin, end) ->
        {
            beginMonths.add(begin);
            endMonths.add(end);
        });
        Assertions.assertEquals(9, beginMonths.size());
        Assertions.assertEquals(9, endMonths.size());

        Assertions.assertEquals(2019, beginMonths.get(8).getYear());
        Assertions.assertEquals(Month.FEBRUARY, beginMonths.get(8).getMonth());
        Assertions.assertEquals(1, beginMonths.get(8).getDayOfMonth());
        Assertions.assertEquals(2019, endMonths.get(8).getYear());
        Assertions.assertEquals(Month.FEBRUARY, endMonths.get(8).getMonth());
        Assertions.assertEquals(28, endMonths.get(8).getDayOfMonth());

        Assertions.assertEquals(2019, beginMonths.get(0).getYear());
        Assertions.assertEquals(Month.OCTOBER, beginMonths.get(0).getMonth());
        Assertions.assertEquals(1, beginMonths.get(0).getDayOfMonth());
        Assertions.assertEquals(2019, endMonths.get(0).getYear());
        Assertions.assertEquals(Month.OCTOBER, endMonths.get(0).getMonth());
        Assertions.assertEquals(31, endMonths.get(0).getDayOfMonth());
    }

    @Test
    void walkYears()
    {
        final var intervalChopper = new IntervalChopper(Pair.of(
                LocalDate.of(2016, 2, 23),
                LocalDate.of(2020, 10, 13)));

        List<LocalDate> beginYears = new ArrayList<>();
        List<LocalDate> endYears = new ArrayList<>();
        intervalChopper.walkYears((begin, end) ->
        {
            beginYears.add(begin);
            endYears.add(end);
        });
        Assertions.assertEquals(5, beginYears.size());
        Assertions.assertEquals(5, endYears.size());

        Assertions.assertEquals(2016, beginYears.get(0).getYear());
        Assertions.assertEquals(Month.JANUARY, beginYears.get(0).getMonth());
        Assertions.assertEquals(1, beginYears.get(0).getDayOfMonth());
        Assertions.assertEquals(2016, endYears.get(0).getYear());
        Assertions.assertEquals(Month.DECEMBER, endYears.get(0).getMonth());
        Assertions.assertEquals(31, endYears.get(0).getDayOfMonth());

        Assertions.assertEquals(2020, beginYears.get(4).getYear());
        Assertions.assertEquals(Month.JANUARY, beginYears.get(4).getMonth());
        Assertions.assertEquals(1, beginYears.get(4).getDayOfMonth());
        Assertions.assertEquals(2020, endYears.get(4).getYear());
        Assertions.assertEquals(Month.DECEMBER, endYears.get(4).getMonth());
        Assertions.assertEquals(31, endYears.get(4).getDayOfMonth());
    }

    @Test
    void walkYearsBack()
    {
        final var intervalChopper = new IntervalChopper(Pair.of(
                LocalDate.of(2016, 2, 23),
                LocalDate.of(2020, 10, 13)));

        List<LocalDate> beginYears = new ArrayList<>();
        List<LocalDate> endYears = new ArrayList<>();
        intervalChopper.walkYearsBack((begin, end) ->
        {
            beginYears.add(begin);
            endYears.add(end);
        });
        Assertions.assertEquals(5, beginYears.size());
        Assertions.assertEquals(5, endYears.size());

        Assertions.assertEquals(2016, beginYears.get(4).getYear());
        Assertions.assertEquals(Month.JANUARY, beginYears.get(4).getMonth());
        Assertions.assertEquals(1, beginYears.get(4).getDayOfMonth());
        Assertions.assertEquals(2016, endYears.get(4).getYear());
        Assertions.assertEquals(Month.DECEMBER, endYears.get(4).getMonth());
        Assertions.assertEquals(31, endYears.get(4).getDayOfMonth());

        Assertions.assertEquals(2020, beginYears.get(0).getYear());
        Assertions.assertEquals(Month.JANUARY, beginYears.get(0).getMonth());
        Assertions.assertEquals(1, beginYears.get(0).getDayOfMonth());
        Assertions.assertEquals(2020, endYears.get(0).getYear());
        Assertions.assertEquals(Month.DECEMBER, endYears.get(0).getMonth());
        Assertions.assertEquals(31, endYears.get(0).getDayOfMonth());
    }

    //test backward
    //walk

    /*@Test
    void walkYearsAndMonth()
    {
        when(intervalChopper.repository.getFirst1Earliest()).thenAnswer((invocation) ->
                List.of(TestCommons.mockTransaction(LocalDate.of(2016, 2, 23))));

        when(intervalChopper.repository.getFirst1Oldest()).thenAnswer((invocation) ->
                List.of(TestCommons.mockTransaction(LocalDate.of(2020, 10, 13))));

        intervalChopper.walkYears((begin, end) ->
        {
            Assertions.assertTrue(false);
        });
    }*/
}

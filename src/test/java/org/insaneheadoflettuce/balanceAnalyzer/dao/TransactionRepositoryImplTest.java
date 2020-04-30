package org.insaneheadoflettuce.balanceAnalyzer.dao;

import org.insaneheadoflettuce.balanceAnalyzer.TestCommons;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class TransactionRepositoryImplTest
{
    TransactionRepositoryImpl extendedRepository;

    @BeforeEach
    void beforeEach()
    {
        extendedRepository = new TransactionRepositoryImpl();
        extendedRepository.repository = mock(TransactionRepository.class);
    }

    @Test
    void findInMonth()
    {
        when(extendedRepository.repository.findByMonth(anyInt(), anyInt())).thenReturn(List.of(new Transaction(), new Transaction()));
        final var result = extendedRepository.findByMonth(LocalDate.of(2020, 1, 3));
        Assertions.assertEquals(2, result.size());
        verify(extendedRepository.repository).findByMonth(1, 2020);
    }

    @Test
    void findInYear()
    {
        when(extendedRepository.repository.findByYear(anyInt())).thenReturn(List.of(new Transaction(), new Transaction()));
        final var result = extendedRepository.findByYear(LocalDate.of(2020, 1, 3));
        Assertions.assertEquals(2, result.size());
        verify(extendedRepository.repository).findByYear(2020);
    }

    @Test
    void getRange()
    {
        when(extendedRepository.repository.getFirstByOrderByValueDateAsc()).thenAnswer((invocation) ->
                Optional.of(TestCommons.mockTransaction(LocalDate.of(2019, 2, 23))));

        when(extendedRepository.repository.getFirstByOrderByValueDateDesc()).thenAnswer((invocation) ->
                Optional.of(TestCommons.mockTransaction(LocalDate.of(2019, 10, 13))));

        final var range = extendedRepository.getRange();
        Assertions.assertTrue(range.isPresent());
        Assertions.assertEquals("2019-02-23", range.get().getFirst().toString());
        Assertions.assertEquals("2019-10-13", range.get().getSecond().toString());
    }

    @Test
    void emptyRange()
    {
        when(extendedRepository.repository.getFirstByOrderByValueDateDesc()).thenReturn(Optional.empty());
        when(extendedRepository.repository.getFirstByOrderByValueDateAsc()).thenReturn(Optional.empty());
        Assertions.assertFalse(extendedRepository.getRange().isPresent());
    }
}

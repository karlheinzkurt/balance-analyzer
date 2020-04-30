package org.insaneheadoflettuce.input.common;

import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.input.api.TransactionFileReader;
import org.insaneheadoflettuce.input.api.TransactionFileReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomogenizingTransactionReader implements TransactionFileReader
{
    private static Logger logger = LoggerFactory.getLogger(HomogenizingTransactionReader.class);
    private List<Transaction> transactions;

    private static class UniqueWrapper
    {
        private Transaction transaction;

        UniqueWrapper(Transaction transaction)
        {
            this.transaction = transaction;
        }

        Transaction get()
        {
            return transaction;
        }

        @Override
        public boolean equals(Object other)
        {
            if (other == this)
            {
                return true;
            }
            if (other == null)
            {
                return false;
            }
            if (getClass() != other.getClass())
            {
                return false;
            }
            return Objects.equals(
                    transaction.getChecksum().hashCode(),
                    ((UniqueWrapper) other).transaction.getChecksum().hashCode());
        }

        @Override
        public int hashCode()
        {
            return transaction.getChecksum().hashCode();
        }

        static List<UniqueWrapper> unify(List<UniqueWrapper> wrappers)
        {
            //wrappers.stream().anyMatch(w -> w.transaction.getState() == Transaction.State.BOOKED)

            /* TODO Currently the lastest wins. But we should
                    - filter for booked, and use the latest booked
                    - when there are only pending, we should use the latest
            * */
            return List.of(wrappers.get(wrappers.size() - 1));
        }
    }

    public HomogenizingTransactionReader(List<Path> paths, TransactionFileReaderFactory readerFactory)
    {
        final var map = paths.stream()
                .map(readerFactory::create)
                .map(TransactionFileReader::read)
                .flatMap(List::stream)
                .map(UniqueWrapper::new)
                .collect(Collectors.groupingBy(UniqueWrapper::hashCode));

        final var count = map.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> entry.setValue(UniqueWrapper.unify(entry.getValue())))
                .count();

        /* TODO We should check here if we have at least one transaction overlapping
                between files. When this is not the case, we should fail to avoid gaps
                in-between imported files.
        * */

        logger.info("Number of unified transactions: " + count);

        transactions = map.values().stream()
                .flatMap(Collection::stream)
                .map(UniqueWrapper::get)
                .sorted(Comparator.comparing(Transaction::getValueDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> read()
    {
        return transactions;
    }
}

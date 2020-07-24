package org.insaneheadoflettuce.balanceAnalyzer;

import com.google.gson.Gson;
import org.insaneheadoflettuce.balanceAnalyzer.dao.AccountRepository;
import org.insaneheadoflettuce.balanceAnalyzer.dao.ClusterDescriptionRepository;
import org.insaneheadoflettuce.balanceAnalyzer.dao.ClusterRepository;
import org.insaneheadoflettuce.balanceAnalyzer.dao.TransactionRepository;
import org.insaneheadoflettuce.balanceAnalyzer.model.Account;
import org.insaneheadoflettuce.balanceAnalyzer.model.Cluster;
import org.insaneheadoflettuce.balanceAnalyzer.model.ClusterDescription;
import org.insaneheadoflettuce.balanceAnalyzer.model.Transaction;
import org.insaneheadoflettuce.balanceAnalyzer.utility.FileUtilities;
import org.insaneheadoflettuce.input.DataImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackageClasses = {BalanceAnalyzerApplication.class, DataImporter.class})
public class BalanceAnalyzerApplication implements CommandLineRunner
{
    Logger logger = LoggerFactory.getLogger(BalanceAnalyzerApplication.class);

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ClusterDescriptionRepository clusterDescriptionRepository;

    @Autowired
    ClusterRepository clusterRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    DataImporter dataImporter;

    @Value("${BALANCE_ANALYZER_CONFIG_ROOT:./config/}")
    Path configRoot;

    @Value("${BALANCE_ANALYZER_DATA_ROOT:./data/}")
    Path dataRoot;

    @Override
    public void run(String... args) throws Exception
    {
        final var clusterDescriptions = FileUtilities.readJson(FileUtilities.ensureFile(configRoot.resolve("cluster_descriptions.json")), ClusterDescription[].class, () -> new ClusterDescription[]{});
        clusterDescriptionRepository.saveAll(clusterDescriptions);

        final var accounts = FileUtilities.readJson(FileUtilities.ensureFile(configRoot.resolve("accounts.json")), Account[].class, () -> new Account[]{});
        accountRepository.saveAll(accounts);

        final var transactions = dataImporter.importAll(dataRoot, accounts);
        logger.info("Number of transactions: " + transactions.size());

        final var transactionsPerAccount = transactions.stream().collect(Collectors.groupingBy(Transaction::getAccount));
        transactionsPerAccount.forEach((key, value) -> logger.info("Number of transactions for account '" + key + "':" + value.size()));

        final var metaClusters = clusterDescriptions.stream()
                .map(ClusterDescription::getMeta)
                .filter(Objects::nonNull).distinct()
                .collect(Collectors.toMap(Function.identity(), name -> new ArrayList<Cluster>()));

        final var clusters = clusterDescriptions.stream().map(description ->
        {
            final var cluster = Cluster.create(description.getName(), true, description.consumeMatching(transactions));
            Optional.ofNullable(metaClusters.get(description.getMeta())).ifPresent(d -> d.add(cluster));
            return cluster;
        }).collect(Collectors.toList());

        clusters.add(Cluster.create("All", false, transactions));
        clusters.add(Cluster.create("Clustered", false, transactions.stream()
                .filter(Transaction::isClustered)
                .collect(Collectors.toList())));
        clusters.add(Cluster.create("Unclustered", false, transactions.stream()
                .filter(Predicate.not(Transaction::isClustered))
                .collect(Collectors.toList())));
        clusters.addAll(metaClusters.entrySet().stream()
                .map(entry -> Cluster.create(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        transactionRepository.saveAll(transactions);
        clusterRepository.saveAll(clusters);

        /*  TODO Support find all transactions with same property
         *  TODO Support different accounts clustered together and separately
         */
        logger.info("Latest observed transaction: " + transactionRepository.getFirstByOrderByValueDateDesc().orElse(Transaction.UNDEFINED).toString());
        logger.info("Latest booked transaction:   " + transactionRepository.getFirstByStateOrderByValueDateDesc(Transaction.State.BOOKED).orElse(Transaction.UNDEFINED).toString());
    }

    public static void main(String[] args)
    {
        SpringApplication.run(BalanceAnalyzerApplication.class, args);
    }
}

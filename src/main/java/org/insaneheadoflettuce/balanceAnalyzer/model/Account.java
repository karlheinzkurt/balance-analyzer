package org.insaneheadoflettuce.balanceAnalyzer.model;

import jakarta.persistence.*;
import org.insaneheadoflettuce.balanceAnalyzer.utility.IBANValidator;

import java.util.Set;

@Entity
@SuppressWarnings("unused")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String institution;
    private String iban;
    private String color;
    @OneToMany(mappedBy = "account")
    private Set<Transaction> transactions;

    public static Account create(String name, String institution, String iban, String color) {
        IBANValidator.validOrThrow(iban);

        final var account = new Account();
        account.name = name;
        account.institution = institution;
        account.iban = iban;
        account.color = color;
        return account;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return iban.substring(12);
    }

    public String getName() {
        return name;
    }

    public String getInstitution() {
        return institution;
    }

    public String getIban() {
        return iban;
    }

    public Integer getNumberOfTransactions() {
        return transactions.size();
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name + " - " + iban;
    }
}

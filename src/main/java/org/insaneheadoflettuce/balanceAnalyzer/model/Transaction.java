package org.insaneheadoflettuce.balanceAnalyzer.model;

import org.insaneheadoflettuce.balanceAnalyzer.Number;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Transaction
{
    public enum State
    {
        UNDEFINED, PENDING, BOOKED
    }

    public static final Transaction UNDEFINED = new Transaction();

    static
    {
        final var undefined = "undefined";
        UNDEFINED.setId(-1L);
        UNDEFINED.setChecksum(undefined);
        UNDEFINED.setValueDate(LocalDate.MIN);
        UNDEFINED.setPostingText(undefined);
        UNDEFINED.setPurpose(undefined);
        UNDEFINED.setRecipientOrPayer(undefined);
        UNDEFINED.setAmount(0.);
        UNDEFINED.setState(State.UNDEFINED);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String checksum;
    @ManyToOne
    private Account account;
    private LocalDate valueDate;
    private String postingText;
    private String purpose;
    private String recipientOrPayer;
    private Double amount;
    private State state;
    private Boolean clustered = false;
    @ManyToMany(mappedBy = "transactions")
    private Set<Cluster> clusters = new HashSet<>();

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public LocalDate getValueDate()
    {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate)
    {
        this.valueDate = valueDate;
    }

    public String getPostingText()
    {
        return this.postingText;
    }

    public void setPostingText(String postingText)
    {
        this.postingText = postingText.trim();
    }

    public String getPurpose()
    {
        return purpose;
    }

    public void setPurpose(String purpose)
    {
        this.purpose = purpose.trim();
    }

    public String getRecipientOrPayer()
    {
        return this.recipientOrPayer;
    }

    public void setRecipientOrPayer(String recipientOrPayer)
    {
        this.recipientOrPayer = recipientOrPayer.trim();
    }

    public Number getAmount()
    {
        return new Number(amount);
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public Boolean isClustered()
    {
        return clustered;
    }

    public String getAccountColor()
    {
        return account.getColor();
    }

    @Override
    public String toString()
    {
        return "Id: " + getId() + ", Value date: " + getValueDate() + ", Recipient or payer: " + getRecipientOrPayer() + ", Purpose: " + getPurpose() + ", Checksum: " + getChecksum();
    }

    public Transaction add(Cluster cluster)
    {
        if (clustered && cluster.isConsuming())
        {
            throw new IllegalStateException("Already clustered transaction has been matched by: " + cluster.getName());
        }
        clusters.add(cluster);
        clustered = clustered ? clustered : cluster.isConsuming();
        return this;
    }
}

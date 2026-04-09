package edu.eci.dosw.tdd.persistence.nonrelational.document;

import edu.eci.dosw.tdd.core.model.Status;

import java.time.LocalDate;

public class LoanHistory {

    private Status status;
    private LocalDate executedAt;

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDate executedAt) { this.executedAt = executedAt; }
}

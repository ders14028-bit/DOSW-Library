package edu.eci.dosw.tdd.persistence.nonrelational.document;

public class BookAvailability {

    private String status;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer loanedCopies;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }

    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }

    public Integer getLoanedCopies() { return loanedCopies; }
    public void setLoanedCopies(Integer loanedCopies) { this.loanedCopies = loanedCopies; }
}

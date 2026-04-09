package edu.eci.dosw.tdd.persistence.nonrelational.document;

public class BookMetadata {

    private Integer pages;
    private String language;
    private String publisher;

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
}

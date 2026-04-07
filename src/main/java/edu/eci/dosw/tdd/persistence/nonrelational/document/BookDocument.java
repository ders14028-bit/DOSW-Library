package edu.eci.dosw.tdd.persistence.nonrelational.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "books")
public class BookDocument {

    @Id
    private String id;
    private String title;
    private String author;

    private List<String> categories;
    private String publicationType;
    private LocalDate publicationDate;
    private String isbn;
    private BookMetadata metadata;
    private BookAvailability availability;
    private LocalDate addedToCatalogAt;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public String getPublicationType() { return publicationType; }
    public void setPublicationType(String publicationType) { this.publicationType = publicationType; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public BookMetadata getMetadata() { return metadata; }
    public void setMetadata(BookMetadata metadata) { this.metadata = metadata; }

    public BookAvailability getAvailability() { return availability; }
    public void setAvailability(BookAvailability availability) { this.availability = availability; }

    public LocalDate getAddedToCatalogAt() { return addedToCatalogAt; }
    public void setAddedToCatalogAt(LocalDate addedToCatalogAt) { this.addedToCatalogAt = addedToCatalogAt; }
}
}

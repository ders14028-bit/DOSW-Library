package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;

public final class UserDocumentMapper {

    private UserDocumentMapper() {}

    public static User toDomain(UserDocument doc) {
        User user = new User();
        user.setId(doc.getId());
        user.setName(doc.getName());
        user.setUsername(doc.getUsername());
        user.setPassword(doc.getPassword());
        user.setRole(doc.getRole());
        return user;
    }

    public static UserDocument toDocument(User user) {
        UserDocument doc = new UserDocument();
        doc.setId(user.getId());
        doc.setName(user.getName());
        doc.setUsername(user.getUsername());
        doc.setPassword(user.getPassword());
        doc.setRole(user.getRole());
        return doc;
    }
}

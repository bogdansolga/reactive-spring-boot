package net.safedata.reactive.intro.dto;

import java.io.Serializable;

public class PostDTO implements Serializable {

    private String author;
    private String message;

    public PostDTO() {
    }

    public PostDTO(String author, String message) {
        this.author = author;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

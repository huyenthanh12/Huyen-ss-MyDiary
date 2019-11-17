package com.example.mydiary.Model;

import java.io.Serializable;
import java.util.Date;

public class HistoryDetail implements Serializable {
    private String id;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateEditted() {
        return dateEditted;
    }

    public void setDateEditted(Date dateEditted) {
        this.dateEditted = dateEditted;
    }

    private Date dateEditted;
}

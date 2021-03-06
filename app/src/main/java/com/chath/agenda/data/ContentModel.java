package com.chath.agenda.data;

public class ContentModel {
    private int id;
    private String title;
    private String description;
    private String created_at;

    public ContentModel(int id, String title, String description, String created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.created_at = created_at;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String create_at) {
        this.created_at = create_at;
    }
}

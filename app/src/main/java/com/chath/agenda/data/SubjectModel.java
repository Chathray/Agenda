package com.chath.agenda.data;

public class SubjectModel {
    private int id;
    private String title;
    private String created_at;

    public SubjectModel(int id, String title, String created_at) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
    }

    public SubjectModel(String title, String created_at) {
        this.title = title;
        this.created_at = created_at;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String create_at) {
        this.created_at = create_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

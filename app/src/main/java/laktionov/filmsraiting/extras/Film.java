package laktionov.filmsraiting.extras;

import com.google.gson.annotations.Expose;

public abstract class Film {

    @Expose
    private int id;
    @Expose
    private String poster_path;
    @Expose
    private String original_title;
    @Expose
    private String original_name;

    public Film(int id, String poster_path) {
        this.id = id;
        this.poster_path = poster_path;
    }

    public Film() {

    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }
    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

}

package laktionov.filmsraiting.extras;

public abstract class Film {

    private int id;
    private String poster_path;
    private String original_title;
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

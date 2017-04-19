package laktionov.filmsraiting.rest.model;


import laktionov.filmsraiting.extras.Film;

public class Poster extends Film {


    @Override
    public String toString() {
        return "Poster{" +
                "id=" + getId() +
                ", poster_path='" + getPoster_path() + '\'' +
                ", original_title='" + getOriginal_title() + '\'' +
                '}';
    }
}

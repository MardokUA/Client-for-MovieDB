package laktionov.filmsraiting.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @Expose
    @SerializedName("results")
    private List<Poster> posterList;
    @Expose
    private Integer total_results;
    @Expose
    private Integer total_pages;

    public MovieResponse() {
    }

    public List<Poster> getPosterList() {
        return posterList;
    }

    public void setPosterList(List<Poster> posterList) {
        this.posterList = posterList;
    }

    public Integer getTotal_results() {
        return total_results;
    }

    public void setTotal_results(Integer total_results) {
        this.total_results = total_results;
    }

    public Integer getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }
}

package laktionov.filmsraiting.rest;

import laktionov.filmsraiting.BuildConfig;
import laktionov.filmsraiting.rest.model.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApi {

    //variable {film} change state from "movie" to "tv_show"

    String API_URL_BASE = "https://api.themoviedb.org/";
    String API_SEARCH_FILM = "/3/search/{film}?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US";
    String API_GET_TOP_RATED = "/3/{film}/top_rated?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US";
    String API_GET_POPULAR = "/3/{film}/popular?api_key=" + BuildConfig.THE_MOVIEDB_API_KEY + "&language=en-US";

    String API_URL_MOVIE_DETAILS = "/movie/";
    String API_URL_TVSHOW_DETAILS = "/tv/";
    String API_URL_IMAGE = "https://image.tmdb.org/t/p/w500";

    @GET(API_SEARCH_FILM)
    Call<MovieResponse> searchFilms(@Path("film") String film,
                                    @Query("page") String pageNumber,
                                    @Query("query") String request);

    @GET(API_GET_TOP_RATED)
    Call<MovieResponse> getTopRated(@Path("film") String film,
                                    @Query("pageNumber") String pageNumber);

    @GET(API_GET_POPULAR)
    Call<MovieResponse> getPopular(@Path("film") String film,
                                   @Query("page") String pageNumber);
}

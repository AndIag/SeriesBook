package es.coru.andiag.seriesbook.utils;

/**
 * Created by andyqm on 21/03/2016.
 */
public class API {

    private static final String API_KEY = "ff32714855b7880b4c72839d1a402dc9";

    private static final String BASE_URL_IMAGES = "http://image.tmdb.org/t/p/";
    private static final String BASE_URL_API = "http://api.themoviedb.org/3";

    private static final String ENDPOINT_SEARCH = "/search/tv";

    // SEARCH PARAMS
    private static final String PARAM_KEYWORDS = "query";
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_PAGE_SIZE = "page";
    private static final String PARAM_LANGUAGE = "language";

    private static final String SIZE_POSTER = "w185";
    private static final String SIZE_BACKDROP = "w300";

    public static String getSearchUrl(String keywords){
        return BASE_URL_API+ENDPOINT_SEARCH+"?"+PARAM_KEYWORDS+"="+keywords+"&"+PARAM_API_KEY+"="+API_KEY;
    }

    public static String getSearchUrl(String keywords, int pageSize){
        return BASE_URL_API+ENDPOINT_SEARCH+"?"
                +PARAM_KEYWORDS+"="+keywords+"&"
                +PARAM_PAGE_SIZE+"="+pageSize+"&"
                +PARAM_API_KEY+"="+API_KEY;
    }

    public static String getSearchUrl(String keywords, String lang, int pageSize){
        return BASE_URL_API+ENDPOINT_SEARCH+"?"
                +PARAM_KEYWORDS+"="+keywords+"&"
                +PARAM_LANGUAGE+"="+lang+"&"
                +PARAM_PAGE_SIZE+"="+pageSize+"&"
                +PARAM_API_KEY+"="+API_KEY;
    }

    public static String getImagePosterUrl(String file){
        return BASE_URL_IMAGES+SIZE_POSTER+file;
    }

    public static String getImageBackdropUrl(String file){
        return BASE_URL_IMAGES+SIZE_BACKDROP+file;
    }

}

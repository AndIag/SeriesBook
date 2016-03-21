package es.coru.andiag.seriesbook.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by andyqm on 21/03/2016.
 */
public class VolleyHelper {
    private static VolleyHelper INSTANCE;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Context context;

    private VolleyHelper(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
        imageLoader = getImageLoader();
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new VolleyHelper(context);
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(50);


                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
        }
        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

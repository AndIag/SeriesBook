package es.coru.andiag.seriesbook.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.entities.gson.SearchResults;
import es.coru.andiag.seriesbook.utils.API;
import es.coru.andiag.seriesbook.utils.VolleyHelper;

public class CreateSerieActivity extends BaseActivity {

    private final Gson gson = new Gson();

    private SearchResults results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_serie);

        Log.d("SEARCH_URL",API.getSearchUrl("walking"));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API.getSearchUrl("walking"), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        results = gson.fromJson(response.toString(), SearchResults.class);
                        Log.d("RESULTs","Total Results : "+results.getTotalResults());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateSerieActivity.this, "Network Error", Toast.LENGTH_LONG).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyHelper.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }
}

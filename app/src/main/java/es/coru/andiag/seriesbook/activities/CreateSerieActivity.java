package es.coru.andiag.seriesbook.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import es.coru.andiag.seriesbook.adapter.ResultsAdapter;
import es.coru.andiag.seriesbook.entities.gson.SearchResults;
import es.coru.andiag.seriesbook.utils.API;
import es.coru.andiag.seriesbook.utils.VolleyHelper;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class CreateSerieActivity extends BaseActivity {

    private final Gson gson = new Gson();

    private SearchResults results;

    private RecyclerView recyclerView;

    private ResultsAdapter adapter;
    private AlphaInAnimationAdapter alphaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_serie);
        adapter = new ResultsAdapter(this);
        alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(false);


        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(3,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(alphaAdapter);

        loadResults("walking");
    }


    private void loadResults(String keywords){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API.getSearchUrl(keywords), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        results = gson.fromJson(response.toString(), SearchResults.class);
                        adapter.updateResults(results.getResults());
                        alphaAdapter.notifyDataSetChanged();
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

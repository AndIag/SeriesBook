package es.coru.andiag.seriesbook.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.entities.gson.Result;
import es.coru.andiag.seriesbook.utils.API;
import es.coru.andiag.seriesbook.utils.VolleyHelper;

/**
 * Created by andyqm on 29/03/2016.
 */
public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "ResultsAdapter";

    private Context context;
    private List<Result> resultList;

    public ResultsAdapter(Context context) {
        this.context = context;
        this.resultList = new ArrayList<>();
    }

    public void updateResults(List<Result> update){
        resultList.clear();
        resultList.addAll(update);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_pick_serie, parent, false);
        return new ResultItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Result item = resultList.get(position);

        ResultItemHolder h = (ResultItemHolder) holder;

        h.textTitle.setText(item.getName());
        h.imagePoster.setDefaultImageResId(R.drawable.no_poster);
        h.imagePoster.setImageUrl(API.getImagePosterUrl(item.getPosterPath()), VolleyHelper.getInstance(context).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    // ViewHolder Inner Class
    class ResultItemHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        NetworkImageView imagePoster;
        View v;

        public ResultItemHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            textTitle = (TextView) v.findViewById(R.id.textTitle);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imagePoster = (NetworkImageView) v.findViewById(R.id.imagePoster);
        }
    }
}

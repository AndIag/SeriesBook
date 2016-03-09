package es.coru.andiag.seriesbook.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vi.swipenumberpicker.OnValueChangeListener;
import com.vi.swipenumberpicker.SwipeNumberPicker;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.db.DAO;
import es.coru.andiag.seriesbook.entities.Series;

/**
 * Created by iagoc on 22/02/2016.
 */
public class SeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "SeriesAdapter";

    private final static int NO_SEASON_IMAGE_ITEM = 0;
    private final static int NO_IMAGE_ITEM = 1;
    private final static int NO_SEASON_ITEM = 2;
    private final static int COMPLETE_ITEM = 3;

    private Context context;
    private Fragment fragment;
    private List<Series> seriesList;

    public SeriesAdapter(Context context, Fragment fragment) {
        this.fragment = fragment;
        this.context = context;
        this.seriesList = new ArrayList<>();
    }

    public void updateSeries(List<Series> series) {
        seriesList.clear();
        seriesList.addAll(series);
        notifyDataSetChanged();
    }

    public void addSeries(Series series, int position) {
        seriesList.add(position, series);
        notifyItemInserted(position);
    }

    public void addSeries(Series series) {
        addSeries(series, seriesList.size());
    }

    public void removeSeries(int position) {
        seriesList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        Series series = getItem(position);
        if ((series.getImageUrl() == null || series.getImageUrl().equals("")) && series.getSeason() == -1) {
            return NO_SEASON_IMAGE_ITEM;
        }
        if (series.getImageUrl() == null || series.getImageUrl().equals("")) {
            return NO_IMAGE_ITEM;
        }
        if (series.getSeason() == -1) {
            return NO_SEASON_ITEM;
        }
        return COMPLETE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == NO_SEASON_IMAGE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_chap_noimg, parent, false);
            return new NoSeasonImageSeriesItem(itemView);
        }
        if (viewType == NO_IMAGE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_s_chap_noimg, parent, false);
            return new NoImageSeriesItem(itemView);
        }
        if (viewType == NO_SEASON_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_chap_img, parent, false);
            return new NoSeasonSearieItem(itemView);
        }
        if (viewType == COMPLETE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_s_chap_img, parent, false);
            return new CompleteSeriesItem(itemView);
        }

        //By default we return the simplest layout
        return new NoSeasonImageSeriesItem(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_serie_chap_noimg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Series series = getItem(position);

        ((NoSeasonImageSeriesItem) holder).textTitle.setText(series.getName());
        ((NoSeasonImageSeriesItem) holder).chapterPicker.setValue(series.getChapter(), false);

        if (holder instanceof NoImageSeriesItem) {
            ((NoImageSeriesItem) holder).seasonPicker.setValue(series.getSeason(), false);
        }
        if (holder instanceof NoSeasonSearieItem) {
            //Añadir la carga de la imagen con volley
        }
        if (holder instanceof CompleteSeriesItem) {
            ((CompleteSeriesItem) holder).seasonPicker.setValue(series.getChapter(), false);
            //Añadir la carga de la imagen con volley
        }
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public Series getItem(int position) {
        return seriesList.get(position);
    }

    //region ViewHolders
    class NoSeasonImageSeriesItem extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        SwipeNumberPicker chapterPicker;
        View v;

        public NoSeasonImageSeriesItem(View itemView) {
            super(itemView);
            this.v = itemView;
            textTitle = (TextView) v.findViewById(R.id.textTitle);
            chapterPicker = (SwipeNumberPicker) v.findViewById(R.id.picker_chapter);
            chapterPicker.setOnValueChangeListener(new OnValueChangeListener() {
                @Override
                public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
                    if (newValue <= view.getMaxValue() && newValue >= view.getMinValue()) {
                        return DAO.getInstance(context).updateSerieChapter(seriesList.get(getAdapterPosition()), newValue);
                    }
                    return false;
                }
            });
            cardView = (CardView) v.findViewById(R.id.card_view);
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (DAO.getInstance(context).removeSerie(getItem(getAdapterPosition()).getId())) {
                        Toast.makeText(context, context.getResources().getString(R.string.removing_serie), Toast.LENGTH_SHORT).show();
                        removeSeries(getAdapterPosition());
                    }
                    return false;
                }
            });
        }
    }

    class NoImageSeriesItem extends NoSeasonImageSeriesItem {
        SwipeNumberPicker seasonPicker;

        public NoImageSeriesItem(View itemView) {
            super(itemView);
            seasonPicker = (SwipeNumberPicker) v.findViewById(R.id.picker_season);
            seasonPicker.setOnValueChangeListener(new OnValueChangeListener() {
                @Override
                public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
                    if (newValue <= view.getMaxValue() && newValue >= view.getMinValue()) {
                        return DAO.getInstance(context).updateSerieSeason(seriesList.get(getAdapterPosition()), newValue);
                    }
                    return false;
                }
            });
        }
    }

    class NoSeasonSearieItem extends NoSeasonImageSeriesItem {
        ImageView image;

        public NoSeasonSearieItem(View itemView) {
            super(itemView);
            image = (ImageView) v.findViewById(R.id.imagePoster);
        }
    }

    class CompleteSeriesItem extends NoSeasonImageSeriesItem {
        SwipeNumberPicker seasonPicker;
        ImageView image;

        public CompleteSeriesItem(View view) {
            super(view);
            seasonPicker = (SwipeNumberPicker) v.findViewById(R.id.picker_season);
            seasonPicker.setOnValueChangeListener(new OnValueChangeListener() {
                @Override
                public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
                    if (newValue <= view.getMaxValue() && newValue >= view.getMinValue()) {
                        return DAO.getInstance(context).updateSerieSeason(seriesList.get(getAdapterPosition()), newValue);
                    }
                    return false;
                }
            });
            image = (ImageView) v.findViewById(R.id.imagePoster);
        }
    }
    //endregion
}

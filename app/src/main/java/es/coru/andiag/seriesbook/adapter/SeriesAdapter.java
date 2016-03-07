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
import es.coru.andiag.seriesbook.entities.Serie;

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
    private List<Serie> seriesList;

    public SeriesAdapter(Context context, Fragment fragment, List<Serie> seriesList) {
        this.seriesList = seriesList;
        this.context = context;
        this.fragment = fragment;
    }

    public SeriesAdapter(Context context, Fragment fragment) {
        this.fragment = fragment;
        this.context = context;
        this.seriesList = new ArrayList<>();
    }

    public void updateSeries(List<Serie> series) {
        seriesList.clear();
        seriesList.addAll(series);
        notifyDataSetChanged();
    }

    public void addSerie(Serie serie, int position) {
        seriesList.add(position, serie);
        notifyItemInserted(position);
    }

    public void addSerie(Serie serie) {
        addSerie(serie, 0);
    }

    public void removeSerie(int position) {
        seriesList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        Serie serie = getItem(position);
        if ((serie.getImageUrl() == null || serie.getImageUrl().equals("")) && serie.getSeason() == -1) {
            return NO_SEASON_IMAGE_ITEM;
        }
        if (serie.getImageUrl() == null || serie.getImageUrl().equals("")) {
            return NO_IMAGE_ITEM;
        }
        if (serie.getSeason() == -1) {
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
            return new NoSeasonImageSerieItem(itemView);
        }
        if (viewType == NO_IMAGE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_s_chap_noimg, parent, false);
            return new NoImageSerieItem(itemView);
        }
        if (viewType == NO_SEASON_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_chap_img, parent, false);
            return new NoSeasonSearieItem(itemView);
        }
        if (viewType == COMPLETE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_serie_s_chap_img, parent, false);
            return new CompleteSerieItem(itemView);
        }

        //By default we return the simplest layout
        return new NoSeasonImageSerieItem(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_serie_chap_noimg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Serie serie = getItem(position);

        ((NoSeasonImageSerieItem) holder).position = position;
        ((NoSeasonImageSerieItem) holder).textTitle.setText(serie.getName());
        ((NoSeasonImageSerieItem) holder).chapterPicker.setValue(serie.getChapter(), false);

        if (holder instanceof NoImageSerieItem) {
            ((NoImageSerieItem) holder).seasonPicker.setValue(serie.getSeason(), false);
        }
        if (holder instanceof NoSeasonSearieItem) {
            //Añadir la carga de la imagen con volley
        }
        if (holder instanceof CompleteSerieItem) {
            ((CompleteSerieItem) holder).seasonPicker.setValue(serie.getChapter(), false);
            //Añadir la carga de la imagen con volley
        }
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public Serie getItem(int position) {
        return seriesList.get(position);
    }

    //region ViewHolders
    class NoSeasonImageSerieItem extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        SwipeNumberPicker chapterPicker;
        View v;
        int position;

        public NoSeasonImageSerieItem(View itemView) {
            super(itemView);
            this.v = itemView;
            textTitle = (TextView) v.findViewById(R.id.textTitle);
            chapterPicker = (SwipeNumberPicker) v.findViewById(R.id.picker_chapter);
            chapterPicker.setOnValueChangeListener(new OnValueChangeListener() {
                @Override
                public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
                    if (newValue <= view.getMaxValue() && newValue >= view.getMinValue()) {
                        return DAO.getInstance(context).updateSerieChapter(seriesList.get(position), newValue);
                    }
                    return false;
                }
            });
            cardView = (CardView) v.findViewById(R.id.card_view);
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (DAO.getInstance(context).removeSerie(getItem(position).getId())) {
                        Toast.makeText(context, context.getResources().getString(R.string.removing_serie), Toast.LENGTH_SHORT).show();
                        removeSerie(position);
                    }
                    return false;
                }
            });
        }
    }

    class NoImageSerieItem extends NoSeasonImageSerieItem {
        SwipeNumberPicker seasonPicker;

        public NoImageSerieItem(View itemView) {
            super(itemView);
            seasonPicker = (SwipeNumberPicker) v.findViewById(R.id.picker_season);
            seasonPicker.setOnValueChangeListener(new OnValueChangeListener() {
                @Override
                public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
                    if (newValue <= view.getMaxValue() && newValue >= view.getMinValue()) {
                        return DAO.getInstance(context).updateSerieSeason(seriesList.get(position), newValue);
                    }
                    return false;
                }
            });
        }
    }

    class NoSeasonSearieItem extends NoSeasonImageSerieItem {
        ImageView image;

        public NoSeasonSearieItem(View itemView) {
            super(itemView);
            image = (ImageView) v.findViewById(R.id.imagePoster);
        }
    }

    class CompleteSerieItem extends NoSeasonImageSerieItem {
        SwipeNumberPicker seasonPicker;
        ImageView image;

        public CompleteSerieItem(View view) {
            super(view);
            seasonPicker = (SwipeNumberPicker) v.findViewById(R.id.picker_season);
            seasonPicker.setOnValueChangeListener(new OnValueChangeListener() {
                @Override
                public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
                    if (newValue <= view.getMaxValue() && newValue >= view.getMinValue()) {
                        return DAO.getInstance(context).updateSerieSeason(seriesList.get(position), newValue);
                    }
                    return false;
                }
            });
            image = (ImageView) v.findViewById(R.id.imagePoster);
        }
    }
    //endregion
}

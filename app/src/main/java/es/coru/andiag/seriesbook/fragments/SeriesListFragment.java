package es.coru.andiag.seriesbook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.activities.MainActivity;
import es.coru.andiag.seriesbook.adapter.SeriesAdapter;
import es.coru.andiag.seriesbook.db.DAO;
import es.coru.andiag.seriesbook.entities.Category;
import es.coru.andiag.seriesbook.entities.Serie;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by Canalejas on 01/03/2016.
 */
public class SeriesListFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "SeriesListFragment";
    private final static String ARG_CATEGORY = "category";
    private static MainActivity mainActivity;

    private SeriesAdapter adapter;
    private ScaleInAnimationAdapter slideAdapter;

    private Category category;
    private final MaterialDialog.SingleButtonCallback addSerieDialogCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            EditText serieName = (EditText) dialog.getView().findViewById(R.id.serieNameText);
            boolean a = !serieName.getText().toString().matches("");
            if (a) {
                Toast.makeText(mainActivity,
                        getResources().getString(R.string.creating_serie) + " : " + serieName.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                Serie serie = new Serie();
                serie.setName(serieName.getText().toString());
                serie.setCategory(category);

                serie = DAO.getInstance(mainActivity).addSerie(serie);
                adapter.addSerie(serie);
                slideAdapter.notifyItemInserted(0);

                dialog.dismiss();
            } else {
                TextInputLayout inputLayout = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_category);
                inputLayout.setError(mainActivity.getString(R.string.error_category));
            }
        }
    };

    public static SeriesListFragment newInstance(Category category) {
        Log.d(TAG, "Starting Fragment: " + category.getName());
        SeriesListFragment fragment = new SeriesListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_CATEGORY, category);
    }

    public void onResumeInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                category = (Category) getArguments().getSerializable(ARG_CATEGORY);
            }
        } else {
            category = (Category) savedInstanceState.getSerializable(ARG_CATEGORY);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retrieve necessary data
        onResumeInstanceState(savedInstanceState);

        //Initialize adapter
        adapter = new SeriesAdapter(mainActivity, this);
        slideAdapter = new ScaleInAnimationAdapter(adapter);
        slideAdapter.setFirstOnly(false);

    }

    private List<Serie> getTestSeries() {
        List<Serie> series = new ArrayList<>();
        Serie s1 = new Serie();
        s1.setName("Shameless");
        series.add(s1);
        Serie s2 = new Serie();
        s2.setName("SKINS");
        s2.setSeason(1);
        series.add(s2);
        Serie s3 = new Serie();
        s3.setName("The Walking Dead");
        s3.setImageUrl("url");
        series.add(s3);
        Serie s4 = new Serie();
        s4.setName("The Big Bang Theory");
        s4.setSeason(4);
        s4.setImageUrl("url");
        series.add(s4);

        for (int i = 0; i < 10; i++) {
            Serie s = new Serie();
            s.setName("Serie " + i);
            s.setSeason(i);
            s.setImageUrl("url " + i);
            series.add(s);
        }

        return series;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_series_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(slideAdapter);

        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.add_button);
        imageButton.setOnClickListener(this);

        adapter.updateSeries(getTestSeries());
        //adapter.notifyDataSetChanged();
        slideAdapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        mainActivity.generateMaterialDialog(R.string.creating_serie, R.layout.dialog_add_serie, R.string.create, addSerieDialogCallback);
        //Toast.makeText(mainActivity,"Crear Serie",Toast.LENGTH_SHORT).show();
    }
}

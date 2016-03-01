package es.coru.andiag.seriesbook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.adapter.SeriesAdapter;
import es.coru.andiag.seriesbook.entities.Category;
import jp.wasabeef.recyclerview.animators.adapters.SlideInLeftAnimationAdapter;

/**
 * Created by Canalejas on 01/03/2016.
 */
public class SeriesListFragment extends Fragment {

    private final static String TAG = "SeriesListFragment";
    private final static String ARG_CATEGORY = "category";

    private SeriesAdapter adapter;
    private SlideInLeftAnimationAdapter slideAdapter;

    private Context context;
    private Category category;

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
        this.context = context;
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
        adapter = new SeriesAdapter(context, this);
        slideAdapter = new SlideInLeftAnimationAdapter(adapter);
        slideAdapter.setFirstOnly(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_series_list, container, false);
        return rootView;
    }
}

package com.hajaulee.mobileanime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class VsubContentFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "VsubContentFragment";
    MainActivity mainActivity;
    RecyclerView mRecyclerView;
    List<AnimeCardData> mAnimeCards;
    AnimeCardAdapter myAdapter;
    int sectionNumber;
    public VsubContentFragment() {
        this.mainActivity = (MainActivity)getActivity();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static VsubContentFragment newInstance(int sectionNumber) {
        Log.d(TAG, "VSection:" + sectionNumber);
        VsubContentFragment fragment = new VsubContentFragment();
        fragment.sectionNumber = sectionNumber;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if (getArguments() != null) {
            this.mainActivity = (MainActivity)getActivity();
            mRecyclerView = rootView.findViewById(R.id.recyclerview);
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), Tool.PORTRAIT_COL_COUNT);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mRecyclerView.addOnScrollListener(Tool.getScrollListener(mainActivity));
            if (myAdapter == null) {
                mAnimeCards = new ArrayList<>();
                List<AnimeCardData> mList = mainActivity.getTotalVsubAnimeList();
                int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
                Log.d(TAG, sectionNumber + "-Vsub:" + mList.size());
                if (mList.size() > 24) {
                    switch (sectionNumber) {
                        case 1:
                            mAnimeCards.addAll(mList.subList(0, 10));
                            break;
                        case 3:
                            mAnimeCards.addAll(mList.subList(10, 20));
                            break;
                        case 2:
                            mAnimeCards.addAll(mList.subList(20, mList.size()));
                            break;
                    }
                }
                myAdapter = new AnimeCardAdapter(getContext(), mAnimeCards);
            }
            mRecyclerView.setAdapter(myAdapter);
        }
        return rootView;
    }
}
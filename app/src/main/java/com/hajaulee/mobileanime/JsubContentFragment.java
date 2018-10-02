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

public class JsubContentFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "JsubContentFragment";
    private MainActivity mainActivity;
    RecyclerView mRecyclerView;
    List<AnimeCardData> mAnimeCards;
    AnimeCardAdapter myAdapter;

    public JsubContentFragment() {
        this.mainActivity = (MainActivity) getActivity();
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static JsubContentFragment newInstance(int sectionNumber) {
        Log.d(TAG, "Section:" + sectionNumber);
        JsubContentFragment fragment = new JsubContentFragment();
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
            this.mainActivity = (MainActivity) getActivity();
            mRecyclerView = rootView.findViewById(R.id.recyclerview);
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), Tool.PORTRAIT_COL_COUNT);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mRecyclerView.addOnScrollListener(Tool.getScrollListener(mainActivity));
            if (myAdapter == null || myAdapter.getItemCount() == 0 || mAnimeCards == null) {
                Log.d("Had Data", "Not data" + getArguments().getInt(ARG_SECTION_NUMBER));
                mAnimeCards = new ArrayList<>();
                List<AnimeCardData> mList = mainActivity.getTotalJsubAnimeList();
                int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
                Log.d(TAG, sectionNumber + "");
                if (mList.size() > 24) {
                    switch (sectionNumber) {
                        case 1:
                            mAnimeCards.addAll(mList.subList(0, 8));
                            break;
                        case 3:
                            mAnimeCards.addAll(mList.subList(8, 16));
                            break;
                        case 2:
                            mAnimeCards.addAll(mList.subList(16, mList.size()));
                            break;
                    }
                }
                myAdapter = new AnimeCardAdapter(getContext(), mAnimeCards);

            } else {
                Log.d("Had Data", "Size:" + myAdapter.getItemCount());
            }
            mRecyclerView.setAdapter(myAdapter);
        }
        return rootView;
    }
}
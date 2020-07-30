package com.matrixdeveloper.battle.club.tournaments.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.android.material.tabs.TabLayout;
import com.matrixdeveloper.battle.club.tournaments.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        Fragment fragment;
        fragment = new PlayFragment();
        loadFragment(fragment);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("ONGOING"));
        tabLayout.addTab(tabLayout.newTab().setText("UPCOMING"));
        tabLayout.addTab(tabLayout.newTab().setText("RESULTS"));
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        assert tab != null;
        tab.select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment;
                switch(tab.getPosition()) {
                    case 0:
                        fragment = new OngoingFragment();
                        loadFragment(fragment);
                        break;
                    case 1:
                        fragment = new PlayFragment();
                        loadFragment(fragment);
                        break;
                    case 2:
                        fragment = new ResultFragment();
                        loadFragment(fragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        SliderLayout mDemoSlider = view.findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        // ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://lh3.googleusercontent.com/proxy/cmTEHUV6Fryx1xbcCEmfZR05ON9mjxqTRVLBSQ7IobHEVXuDg-AnMDCSfpQkNlKfzW7I_GIjeNhLSucmXJ-ZsU4hNuZbZChmVX4vQ9-C");
        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        // listName.add("JPG - Github");
        // listName.add("PNG - Android Studio");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(getContext());
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    //    .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);

            //add your extra information
            sliderView.bundle(new Bundle());
            //  sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
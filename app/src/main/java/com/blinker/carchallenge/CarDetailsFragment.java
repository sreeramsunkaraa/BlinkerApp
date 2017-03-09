package com.blinker.carchallenge;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by apple on 3/7/17.
 */

public class CarDetailsFragment extends Fragment{

    TextView tvYear,tvMake,tvModel;
    ImageView ivIcon;

    public CarDetailsFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_car_detail,container,false);
        tvYear=(TextView)view.findViewById(R.id.tvYear);
        tvMake=(TextView)view.findViewById(R.id.tvMake);
        tvModel=(TextView)view.findViewById(R.id.tvModel);
        ivIcon=(ImageView)view.findViewById(R.id.ivCarIcon);

        return view;
    }

    public void refreshCarDetails(String year, String make, String model, String icon) {
        tvModel.setText(model);
        tvYear.setText(make);
        tvMake.setText(year);

        Picasso.with(getActivity()).load(icon).into(ivIcon);


    }
}

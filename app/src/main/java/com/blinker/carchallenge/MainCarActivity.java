package com.blinker.carchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainCarActivity extends AppCompatActivity implements CarListFragment.CarDetailsListener {

    boolean dualPane=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);


    }


    @Override
    public void setCarDetails(String year, String make, String model, String icon) {
       CarDetailsFragment carDetailsFragment=(CarDetailsFragment)getFragmentManager().findFragmentById(R.id.flCarDetails);
       carDetailsFragment.refreshCarDetails(year,make,model,icon);
    }
}

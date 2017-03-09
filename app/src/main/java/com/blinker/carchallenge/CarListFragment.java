package com.blinker.carchallenge;


import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by apple on 3/7/17.
 */

public class CarListFragment extends ListFragment implements View.OnClickListener {

    ListView lvCarList;

    CarDetailsListener _carDetailsListener;

    ArrayList<HashMap<String, String>> carList;
    HashMap<String, String> mapping;

    LinearLayout llSearchCriteria;

    EditText edtYear, edtMake, edtModel;
    String strYear = "", strMake = "", strModel = "";

    ImageView ivSearch, ivClear;
    Button btnSearch;

    int selected = 0;

    final String YEAR="year",MAKE="make",MODEL="model", IMAGE_URL ="image_url",MILEAGE="mileage";

    private Boolean dialogShownOnceFullName = false;
    static ProgressDialog pdLoading;

    EfficientAdapter efficientAdapter;

    public CarListFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_consult, container, false);

        initializations(view);
        setRetainInstance(true);
        ivSearch.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt("selectedItem");
        }
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivSearch:

                clickSearchCriteria();
                break;
            case R.id.ivClear:

                clickClearIcon();
                defaultValues();
                new GetTheCarList().execute();
                break;

            case R.id.btnSearch:
                boolean anyCarItem = false;
                clickSearchButton();
                if (edtYear.getText().toString().length() > 0) {
                    strYear = edtYear.getText().toString().toLowerCase();
                }
                if (edtModel.getText().toString().length() > 0) {
                    strModel = edtModel.getText().toString().toLowerCase();
                }
                if (edtMake.getText().toString().length() > 0) {
                    strMake = edtMake.getText().toString().toLowerCase();
                }
                for (int i = 0; i < carList.size(); i++) {
                    mapping = carList.get(i);
                    if ((!mapping.get(MODEL).toString().toLowerCase().contains(strModel))
                            || (!mapping.get(MAKE).toString().toLowerCase().contains(strMake))
                            || (!mapping.get(YEAR).toString().toLowerCase().contains(strYear))) {
                        carList.remove(i);
                        i--;

                    } else {
                        anyCarItem = true;
                    }


                }

                if (anyCarItem) {
                    anyCarItem = false;
                    efficientAdapter.notifyDataSetChanged();
                    lvCarList.setAdapter(efficientAdapter);
                    clickSearchButtonWithList();
                } else {
                    Toast.makeText(getActivity(), "No cars available", Toast.LENGTH_SHORT).show();
                    clickClearIcon();
                    defaultValues();
                    new GetTheCarList().execute();
                }
                break;
            default:
                break;

        }
    }

    void clickSearchButtonWithList()
    {
        ivClear.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(View.GONE);
    }
    void clickSearchButton()
    {
        llSearchCriteria.setVisibility(View.GONE);

    }
    void clickClearIcon()
    {
        ivClear.setVisibility(View.GONE);
        llSearchCriteria.setVisibility(View.GONE);
        ivSearch.setVisibility(View.VISIBLE);
    }
    void clickSearchCriteria()
    {
        defaultStringValues();
        ivSearch.setVisibility(View.GONE);
        llSearchCriteria.setVisibility(View.VISIBLE);
    }

    void defaultValues()
    {
        edtYear.setText("");
        edtMake.setText("");
        edtModel.setText("");
    }

    void defaultStringValues()
    {
        strYear="";
        strModel="";
        strMake="";
    }
    void initializations(View view)
    {
        lvCarList=(ListView)view.findViewById(android.R.id.list);
        llSearchCriteria=(LinearLayout)view.findViewById(R.id.llSearchCriteria);
        ivSearch=(ImageView)view.findViewById(R.id.ivSearch);
        ivClear=(ImageView)view.findViewById(R.id.ivClear);
        btnSearch=(Button)view.findViewById(R.id.btnSearch);
        edtMake=(EditText)view.findViewById(R.id.edtMake);
        edtModel=(EditText)view.findViewById(R.id.edtModel);
        edtYear=(EditText)view.findViewById(R.id.edtYear);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItem",selected);
    }



    @Override
    public void onStart() {
        super.onStart();

        if(lvCarList==null || lvCarList.getCount()==0) {
            new GetTheCarList().execute();
        }

        _carDetailsListener=(CarDetailsListener)getActivity();
        lvCarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               HashMap<String,String> map= carList.get(position);

                selected=position;
                if(_carDetailsListener!=null) {
                    _carDetailsListener.setCarDetails(map.get(YEAR), map.get(MAKE), map.get(MODEL), map.get(IMAGE_URL));
                }

            }
        });


    }



    public interface CarDetailsListener
    {
        public void setCarDetails(String year, String make,String model, String icon);
    }


    public class GetTheCarList extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading=ProgressDialog.show(getActivity(),"","Loading...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder sb=new StringBuilder();
            BufferedReader br=null;

            try {
                br=new BufferedReader(new InputStreamReader(getActivity().getAssets().open("vehicles.json")));
                String temp;
                while((temp=br.readLine())!=null)
                {
                    sb.append(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            finally {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }


            JSONArray jsonAr = null;
            try {
                jsonAr = new JSONArray(sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            carList=new ArrayList<>();

            for(int i=0;i<=jsonAr.length();i++)
            {
                mapping=new HashMap<>();
                JSONObject jsonOb= null;
                try {
                    jsonOb = jsonAr.getJSONObject(i);
                    String year=jsonOb.getString(YEAR);
                    String make=jsonOb.getString(MAKE);
                    String model=jsonOb.getString(MODEL);
                    String mileage=jsonOb.getString(MILEAGE);
                    String icon=jsonOb.getString(IMAGE_URL);


                    mapping.put(YEAR,year);
                    mapping.put(MAKE,make);
                    mapping.put(MODEL,model);
                    mapping.put(MILEAGE,mileage);
                    mapping.put(IMAGE_URL,icon);

                    carList.add(mapping);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            efficientAdapter=new EfficientAdapter();
            lvCarList.setAdapter(efficientAdapter);

            lvCarList.performItemClick(null,selected,lvCarList.getFirstVisiblePosition());

            pdLoading.dismiss();
        }
    }

    class Holder
    {
        TextView tvDetails,tvMileage,tvIcon;
    }

    class EfficientAdapter extends BaseAdapter
    {
        Holder holder;
        private LayoutInflater layoutInflater;
        public EfficientAdapter()
        {
            layoutInflater=(LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return carList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null)
            {
                convertView=layoutInflater.inflate(R.layout.list_item,parent,false);
                holder=new Holder();
                holder.tvDetails=(TextView)convertView.findViewById(R.id.tvDetails);
                holder.tvMileage=(TextView) convertView.findViewById(R.id.tvMileage);
                holder.tvIcon=(TextView) convertView.findViewById(R.id.tvIcon);
                convertView.setTag(holder);
            }
            else
            {
                holder=(Holder)convertView.getTag();
            }
            HashMap<String,String> map=carList.get(position);
            String details=map.get(YEAR)+" "+map.get(MAKE)+" "+map.get(MODEL);
            String mileage=map.get(MILEAGE);
            String icon=map.get(IMAGE_URL);
            holder.tvDetails.setText(details);
            holder.tvMileage.setText(mileage);
            holder.tvIcon.setText(icon);

            return convertView;
        }


    }
}

package com.example.sudhanshu.gis;


import org.apache.commons.lang3.StringUtils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import java.lang.Double;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.ganfra.materialspinner.MaterialSpinner;

public class StartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;
    String lat, longi;
    String soil_depth_db,soil_type_db,lulc_type_db,district_db;
    Double slope_db;
    HashMap <String,Double [] > rainfall_db;
    boolean [] dates_check;


    String soiltype[] = {"Select Soil Type","Clay Loam", "Clayey", "Gravelly Clay", "Gravelly Clay Loam", " Gravelly Loam", "Gravelly Sandy Clay Loam ", "Gravelly Sandy Loam", "Gravelly Silty Clay", "Gravelly Silty Loam", "Loamy","Loamy Sand","Sandy","Sandy Clay", "Sandy Clay Loam", "Sandy Loam","Silty Clay", "Silty Clay Loam","Silty Loam"};
    String  landuse[] = {"Select Landuse","Agriculture", "Forest", "Fallow Land", "Habitation", "Scrub", "Waste Land", "Current Fallow"};
    String year[] = {"2013", "2014", "2015", "2016", "2017"};
    Integer irrigation[] = {0,1,2,3,4,5,6,7,8,9,10};
    Integer irrigation_rain[] ={0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};
    String crop[] = {"Cauliflower", "Sorghum", "Onion", "Cotton", "Tomato", "Vegetables", "Chilly", "Sugarcane", "Bajri", "Tur", "Lemon", "Fodder Crop", "Potato", "Pomogranate", "Udid", "Banana", "Groundnut", "Soybean", "Brinjal", "Sweetlime", "Small Vegetable", "Maize", "Sunflower", "Orange", "Moong", "Turmeric", "Grapes"};
    String soil_depth [] = {"Select Soil Depth","Deep (50 to 100 cm)","Very shallow (< 10 cm)","Deep to very deep (> 50 cm)","Habitation Mask","Shallow (10 to 25 cm)","Waterbody Mask","Moderately deep (25 to 50 cm)","Very deep (> 100 cm)","Shallow to very shallow (< 25 cm)"};
    MaterialSpinner soil_sp,landuse_sp,year_sp,crop_sp,soil_depth_sp,irrigation_water_sp;
    public String year_sel;
    EditText slope;
    EditText monsoonend;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        lat = getIntent().getExtras().get("Latitude").toString();
        longi = getIntent().getExtras().get("Longitude").toString();

        button = (Button) StartActivity.this.findViewById(R.id.buttonRun);
        monsoonend = findViewById(R.id.monsoonend);
//        Toast.makeText(StartActivity.this, lat + " " + longi, Toast.LENGTH_LONG).show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, soiltype);
        soil_sp = findViewById(R.id.soiltype);
        soil_sp.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, landuse);
        landuse_sp = findViewById(R.id.landuse);
        landuse_sp.setAdapter(arrayAdapter1);

        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, year);
        year_sp = findViewById(R.id.year);
        year_sp.setAdapter(arrayAdapter2);


        ArrayAdapter<Integer> arrayAdapter3 = new ArrayAdapter<Integer>(this, android.R.layout.simple_dropdown_item_1line, irrigation);
        final MaterialSpinner irrigation = findViewById(R.id.irrigation);
        irrigation.setAdapter(arrayAdapter3);

        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, crop);
        crop_sp = findViewById(R.id.crop);
        crop_sp.setAdapter(arrayAdapter4);

        ArrayAdapter <String> arrayAdapter5 = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,soil_depth);
        soil_depth_sp = findViewById(R.id.soildepth);
        soil_depth_sp.setAdapter(arrayAdapter5);

        final ArrayAdapter<Integer> arrayAdapter6 = new ArrayAdapter<Integer>(this, android.R.layout.simple_dropdown_item_1line, irrigation_rain);
        irrigation_water_sp = findViewById(R.id.irrigation_water);
        irrigation_water_sp.setAdapter(arrayAdapter6);
        irrigation_water_sp.setSelection(35);


        SharedPreferences spf = getSharedPreferences("year_selected", 0);
        SharedPreferences.Editor edit = spf.edit();
        edit.putString("year",  irrigation.getSelectedItem().toString());
        edit.apply();
        slope = (EditText) findViewById(R.id.slope);
        getData(lat, longi);

//        setData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(StartActivity.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                Lookup ll = new Lookup();
                String lulc_sel, soil_sel,crop_sel,soil_dep_sel;
                lulc_sel = (String)landuse_sp.getSelectedItem();
                soil_sel = (String) soil_sp.getSelectedItem();
                crop_sel = (String) crop_sp.getSelectedItem();

                int sowing_threshold = 30;

                soil_dep_sel = (String) soil_depth_sp.getSelectedItem();
                double [] rf = dlbTodouble(rainfall_db.get(year_sel));
                double [] et = {7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 7.51, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.77, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.55, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.78, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 4.8, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.9, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.48, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 3.95, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.17, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 5.29, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 7.03, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25, 8.25};



                int monsoon_end_index = (int) getDifferenceDays( monsoonend.getText().toString(), year_sel);
                int irrigation_water_mm = (int) irrigation_water_sp.getSelectedItem();
//                Log.d("TAG IRRG",Integer.toString(irrigation_water_mm));
                String item = irrigation.getSelectedItem().toString();
                int n = Integer.parseInt(item);
                for (int i=0;i<n;i++){
                    SharedPreferences spfr = getSharedPreferences("dates", Context.MODE_PRIVATE);
                    String dt = spfr.getString("Date_"+Integer.toString(i),"none");
                    int index = (int) getDifferenceDays(dt,year_sel);
                    rf[index]+=irrigation_water_mm;

                }



                Log.d("selected data",lulc_sel+" "+soil_sel+" "+ crop_sel+ " "+ year_sel);
                double depth_sel =ll.soil_depth.get(soil_dep_sel.toLowerCase());
                double slope_sel = Double.parseDouble(slope.getText().toString());

                kharifModel km = new  kharifModel(lulc_sel.toLowerCase(),soil_sel.toLowerCase(), depth_sel, slope_sel, crop_sel,rf,monsoon_end_index );
                km.setup();
                km.pET_calculation(et, sowing_threshold, rf);
                for (int i=0;i<365;i++) {
                    km.primary_runoff(i, rf);
                    km.Aet(i);
                    km.percolation_below_root_zone(i);
                    km.secondary_runoff(i);
                    km.percolation_to_GW(i);
                }
                Intent intent = new Intent(StartActivity.this,ResultActivity.class);
                intent.putExtra("serialize data",km);
                Log.d("PutExtra","Kharif added");
                progressDialog.dismiss();
                startActivity(intent);

            }
        });

        year_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_sel = (String) year_sp.getSelectedItem();
                SharedPreferences spf = getSharedPreferences("year_selected", MODE_WORLD_READABLE);

                SharedPreferences.Editor edit = spf.edit();
                edit.putString("year", year_sel);
                edit.apply();


                monsoonend.setText("10/10/"+year_sel);
//                Toast.makeText(StartActivity.this,year_sel,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        monsoonend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment d = new DateDialog(v,Integer.parseInt(year_sel),9,10, -1);
                d.show(getFragmentManager(),"Date Picker");

            }
        });

        //Toast.makeText(MainActivity.this, "in activity", Toast.LENGTH_LONG).show();
        irrigation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recyclerView = findViewById(R.id.recyclerView);
                String item = irrigation.getItemAtPosition(position).toString();

                int n = Integer.parseInt(item);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(StartActivity.this));

                listItems = new ArrayList<>();
                for(int i=0; i < n;i++){
                    ListItem listItem = new ListItem(
                            "Select Date "+Integer.toString(i+1)
                    );
                    listItems.add(listItem);
                }
                adapter = new MyAdapter(listItems, StartActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData(){
        Log.d("Enterd set","Setdata");
        Lookup l = new Lookup();
        if(Arrays.asList(soil_depth).indexOf(soil_depth_db) != -1)
            soil_depth_sp.setSelection(Arrays.asList(soil_depth).indexOf(soil_depth_db));
        if(Arrays.asList(soiltype).indexOf(soil_type_db) != -1)
            soil_sp.setSelection(Arrays.asList(soiltype).indexOf(soil_type_db));
        String generalizedLandUse = StringUtils.capitalize(l.landuse.get(lulc_type_db.toLowerCase()));
        if(Arrays.asList(landuse).indexOf(generalizedLandUse) != -1)
            landuse_sp.setSelection(Arrays.asList(landuse).indexOf(generalizedLandUse));
//        Toast.makeText(StartActivity.this,slope_db.toString(),Toast.LENGTH_LONG).show();
                slope.setText(slope_db.toString());
//        Log.d("index_soil",soil_depth_db);
//        Log.d("index23",slope_db.toString());
//        Log.d("index_S",soil_depth_db);
//        Log.d("tp",lulc_type_db);
//        Log.d("gpp",generalizedLandUse);
//        Log.d("tpp",Integer.toString(Arrays.asList(landuse).indexOf(generalizedLandUse)));
        year_sel  = getYear_sel();
        monsoonend.setText("10/10/"+year_sel);
        Log.d("Year Sel",year_sel);

    }


    public String getYear_sel() {
        return (String) year_sp.getSelectedItem();
    }


    private void  getData(String lat, String longi){
        String URL = "http://www.cse.iitb.ac.in/jaltarang/test.php?x="+longi+"&y="+lat;
//        String URL = "https://api.github.com/users";
        Log.d("URL",URL);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.d("JSON",response);
                            JSONObject jsonObj = new JSONObject(response);
                            if(jsonObj.get("soil_depth").equals(null))
                                soil_depth_db = "NULL";
                            else
                                soil_depth_db = (String) jsonObj.get("soil_depth");
                            if(jsonObj.get("soil_type").equals(null))
                                soil_type_db = "NULL";
                            else
                                soil_type_db = (String) jsonObj.get("soil_type");
                            if(jsonObj.get("lulc_type").equals(null))
                                lulc_type_db = "NULL";
                            else
                                lulc_type_db = (String) jsonObj.get("lulc_type");
                            if(jsonObj.get("slope").equals(null))
                                slope_db = 1.0 ;
                            else
                                slope_db = new Double((String) jsonObj.get("slope"));
                            Log.d("soil_dep",soil_depth_db);
                            Log.d("soil_type",soil_type_db);
                            Log.d("lulc_t",lulc_type_db);
                            Log.d("slope",slope_db.toString());
                            district_db = (String) jsonObj.get("District");
                            String circle_db = (String) jsonObj.get("Circle");
                            String[] rf2013 =jsontoString((JSONArray)  jsonObj.get("Rainfall2013"));
                            String[] rf2014 =jsontoString((JSONArray)  jsonObj.get("Rainfall2014"));
                            String[] rf2015 =jsontoString((JSONArray)  jsonObj.get("Rainfall2015"));
                            String[] rf2016 =jsontoString((JSONArray)  jsonObj.get("Rainfall2016"));
                            String[] rf2017 =jsontoString((JSONArray)  jsonObj.get("Rainfall2017"));
                            rainfall_db = new HashMap<> ();
                            rainfall_db.put("2013", strtoDouble(rf2013));
                            rainfall_db.put("2014", strtoDouble(rf2014));
                            rainfall_db.put("2015", strtoDouble(rf2015));
                            rainfall_db.put("2016", strtoDouble(rf2016));
                            rainfall_db.put("2017", strtoDouble(rf2017));
                            Toast.makeText(StartActivity.this,circle_db,Toast.LENGTH_LONG).show();
                            setData();

                        } catch (Exception e) {
                            Log.d("Chutiya code","chutiya");
                            e.printStackTrace();
                            }
                        pDialog.dismiss();

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG chutiya","error occured"+error.toString());
                        pDialog.dismiss();

                    }
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue  = Volley.newRequestQueue(StartActivity.this);
        requestQueue.add(stringRequest);

        //        try {
//            JSONObject jsonObj = new JSONObject("{\"soil_type\":\"Clayey\",\"soil_depth\":\"Very deep (> 100 cm)\",\"slope\":\"6.21786785125732\",\"District\":\"Amravati\",\"Taluka\":\"Amravati\",\"Circle\":\"Mahuli\",\"lulc_type\":\"Kharif\",\"Rainfall2013\":[\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"39.8\",\"0.0\",\"18.8\",\"9.4\",\"0.0\",\"11.8\",\"0.0\",\"32.8\",\"27.9\",\"21.1\",\"11.4\",\"25.9\",\"0.0\",\"0.0\",\"1.8\",\"0.0\",\"6.9\",\"11.5\",\"9.6\",\"25.4\",\"10.9\",\"0.0\",\"0.0\",\"0.0\",\"24.6\",\"0.0\",\"5.2\",\"15.2\",\"0.0\",\"0.0\",\"1.7\",\"5.3\",\"0.0\",\"0.0\",\"3.3\",\"11.7\",\"18.0\",\"0.0\",\"14.5\",\"24.6\",\"0.0\",\"13.0\",\"0.0\",\"16.2\",\"0.0\",\"0.0\",\"0.0\",\"6.8\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"39.1\",\"81.8\",\"2.8\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"3.4\",\"18.2\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"8.5\",\"0.0\",\"18.0\",\"9.3\",\"26.6\",\"7.9\",\"4.6\",\"1.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"9.7\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"5.4\",\"53.4\",\"0.0\",\"0.0\",\"0.0\",\"10.9\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"50.5\",\"0.0\",\"0.0\",\"0.0\",\"2.3\",\"0.0\",\"6.9\",\"0.0\",\"5.9\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0.0\"],\"Rainfall2014\":[\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"7.6\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"83.4\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.3\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"27.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"4.8\",\"0.0\",\"0.0\",\"4.0\",\"56.4\",\"0.0\",\"7.4\",\"0.0\",\"24.0\",\"7.2\",\"7.2\",\"204.0\",\"38.4\",\"0.0\",\"0.0\",\"0.0\",\"36.0\",\"3.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"4.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"26.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"46.0\",\"0.0\",\"9.6\",\"6.4\",\"60.0\",\"21.0\",\"61.8\",\"17.0\",\"5.4\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"50.0\",\"39.4\",\"19.4\",\"0.0\",\"0.0\",\"3.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"4.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.1\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"2.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0.0\",\"0\"],\"Rainfall2015\":[\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"3.5\",\"0.0\",\"0.0\",\"0.0\",\"11.0\",\"2.0\",\"34.6\",\"0.0\",\"16.0\",\"6.8\",\"0.0\",\"26.4\",\"14.5\",\"62.2\",\"13.4\",\"17.0\",\"3.4\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.4\",\"8.3\",\"0.0\",\"28.0\",\"42.6\",\"12.0\",\"0.0\",\"0.0\",\"0.0\",\"14.6\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"36.0\",\"156.0\",\"13.0\",\"0.0\",\"4.0\",\"0.0\",\"28.0\",\"0.0\",\"59.2\",\"4.0\",\"24.4\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.0\",\"17.9\",\"7.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"4.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.0\",\"0.0\",\"0.0\",\"5.8\",\"58.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"4.7\",\"0.0\",\"0.0\",\"0.0\",\"8.3\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],\"Rainfall2016\":[\"0.0\",\"0.0\",\"2.4\",\"0.0\",\"0.0\",\"0.0\",\"28.3\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"11.6\",\"2.6\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"24.8\",\"0.0\",\"0.0\",\"10.6\",\"53.4\",\"61.8\",\"15.6\",\"28.5\",\"46.4\",\"46.4\",\"4.3\",\"11.7\",\"3.0\",\"0.0\",\"0.0\",\"69.0\",\"17.2\",\"31.8\",\"12.8\",\"12.1\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"6.0\",\"3.0\",\"2.2\",\"0.0\",\"1.4\",\"13.1\",\"68.5\",\"0.0\",\"11.8\",\"19.4\",\"1.7\",\"0.0\",\"0.0\",\"0.0\",\"22.0\",\"2.9\",\"13.0\",\"7.0\",\"0.0\",\"53.6\",\"0.0\",\"0.0\",\"5.3\",\"4.3\",\"0.0\",\"0.0\",\"0.0\",\"15.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"2.1\",\"1.4\",\"14.5\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"10.0\",\"8.0\",\"5.3\",\"0.0\",\"0.0\",\"0.0\",\"5.3\",\"12.3\",\"0.0\",\"2.0\",\"0.0\",\"10.3\",\"7.3\",\"0.0\",\"0.0\",\"0.0\",\"13.6\",\"22.4\",\"0.5\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"46.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0.0\",\"0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0.0\",\"0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0\",\"0\"],\"Rainfall2017\":[\"0.0\",\"2.2\",\"0.0\",\"0.0\",\"0.0\",\"10.9\",\"8.9\",\"0.0\",\"0.0\",\"0.0\",\"25.2\",\"1.4\",\"0.0\",\"3.3\",\"0.0\",\"17.3\",\"19.2\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"4.2\",\"0.0\",\"0.0\",\"11.4\",\"0.0\",\"47.2\",\"2.1\",\"0.0\",\"6.4\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"5.1\",\"9.6\",\"6.0\",\"2.0\",\"0.0\",\"0.0\",\"0.0\",\"10.3\",\"16.0\",\"4.1\",\"4.0\",\"1.4\",\"10.3\",\"11.7\",\"23.6\",\"8.6\",\"38.4\",\"8.0\",\"5.1\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"4.3\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"16.0\",\"22.4\",\"14.6\",\"0.0\",\"4.6\",\"0.0\",\"1.2\",\"0.0\",\"0.0\",\"0.0\",\"12.5\",\"29.6\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"9.1\",\"0.0\",\"5.3\",\"17.4\",\"6.0\",\"4.1\",\"11.1\",\"3.0\",\"9.4\",\"0.0\",\"0.0\",\"5.1\",\"8.3\",\"35.8\",\"9.2\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"25.6\",\"0.0\",\"0.0\",\"12.4\",\"18.3\",\"0.0\",\"0.0\",\"0.0\",\"21.6\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0.0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]}");
//            if (jsonObj.get("soil_depth").equals(null))
//                soil_depth_db = "NULL";
//            else
//                soil_depth_db = (String) jsonObj.get("soil_depth");
//            if (jsonObj.get("soil_type").equals(null))
//                soil_type_db = "NULL";
//            else
//                soil_type_db = (String) jsonObj.get("soil_type");
//            if (jsonObj.get("lulc_type").equals(null))
//                lulc_type_db = "NULL";
//            else
//                lulc_type_db = (String) jsonObj.get("lulc_type");
//            if (jsonObj.get("slope").equals(null))
//                slope_db = 1.0;
//            else
//                slope_db = new Double((String) jsonObj.get("slope"));
//            Log.d("soil_dep", soil_depth_db);
//            Log.d("soil_type", soil_type_db);
//            Log.d("lulc_t", lulc_type_db);
//            Log.d("slope", slope_db.toString());
//            district_db = (String) jsonObj.get("District");
//            String[] rf2013 = jsontoString((JSONArray) jsonObj.get("Rainfall2013"));
//            String[] rf2014 = jsontoString((JSONArray) jsonObj.get("Rainfall2014"));
//            String[] rf2015 = jsontoString((JSONArray) jsonObj.get("Rainfall2015"));
//            String[] rf2016 = jsontoString((JSONArray) jsonObj.get("Rainfall2016"));
//            String[] rf2017 = jsontoString((JSONArray) jsonObj.get("Rainfall2017"));
//            rainfall_db = new HashMap<>();
//            rainfall_db.put("2013", strtoDouble(rf2013));
//            rainfall_db.put("2014", strtoDouble(rf2014));
//            rainfall_db.put("2015", strtoDouble(rf2015));
//            rainfall_db.put("2016", strtoDouble(rf2016));
//            rainfall_db.put("2017", strtoDouble(rf2017));
//        } catch (JSONException e){
//            e.printStackTrace();
//        }

//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestFuture<JSONObject> requestFuture=RequestFuture.newFuture();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL,null,requestFuture,requestFuture);
//        MySingleton.getInstance(StartActivity.this).addToRequestQueue(jsonObjectRequest);
//            try {
//
////                Log.d("JSON",response.toString());
//                JSONObject jsonObj = requestFuture.get(10,TimeUnit.SECONDS);
//                if(jsonObj.get("soil_depth").equals(null))
//                    soil_depth_db = "NULL";
//                else
//                    soil_depth_db = (String) jsonObj.get("soil_depth");
//                if(jsonObj.get("soil_type").equals(null))
//                    soil_type_db = "NULL";
//                else
//                    soil_type_db = (String) jsonObj.get("soil_type");
//                if(jsonObj.get("lulc_type").equals(null))
//                    lulc_type_db = "NULL";
//                else
//                    lulc_type_db = (String) jsonObj.get("lulc_type");
//                if(jsonObj.get("slope").equals(null))
//                    slope_db = 1.0 ;
//                else
//                    slope_db = new Double((String) jsonObj.get("slope"));
//                Log.d("soil_dep",soil_depth_db);
//                Log.d("soil_type",soil_type_db);
//                Log.d("lulc_t",lulc_type_db);
//                Log.d("slope",slope_db.toString());
//                district_db = (String) jsonObj.get("District");
//                String[] rf2013 =jsontoString((JSONArray)  jsonObj.get("Rainfall2013"));
//                String[] rf2014 =jsontoString((JSONArray)  jsonObj.get("Rainfall2014"));
//                String[] rf2015 =jsontoString((JSONArray)  jsonObj.get("Rainfall2015"));
//                String[] rf2016 =jsontoString((JSONArray)  jsonObj.get("Rainfall2016"));
//                String[] rf2017 =jsontoString((JSONArray)  jsonObj.get("Rainfall2017"));
//                rainfall_db = new HashMap<> ();
//                rainfall_db.put("2013", strtoDouble(rf2013));
//                rainfall_db.put("2014", strtoDouble(rf2014));
//                rainfall_db.put("2015", strtoDouble(rf2015));
//                rainfall_db.put("2016", strtoDouble(rf2016));
//                rainfall_db.put("2017", strtoDouble(rf2017));
//
//
//            } catch (Exception e) {
//                Log.d("Chutiya code","chutiya");
//                e.printStackTrace();
//                }
//            pDialog.dismiss();
//



    }


    static Double [] strtoDouble(String [] arr ) {
        Double [] arrDouble = new Double[arr.length];
        for (int i=0;i<arr.length;i++)
            arrDouble[i]=new Double(arr[i]);
        return arrDouble;
    }

    static String [] jsontoString(JSONArray jsArray) throws JSONException {
        String[] array = new String[jsArray.length()];
        for (int i = 0; i < jsArray.length(); i++)
            array[i] = (String) jsArray.get(i);
        return array;
    }

    static double [] dlbTodouble(Double [] arr ) {
        double [] arrDouble = new double[arr.length];
        for (int i=0;i<arr.length;i++)
            arrDouble[i]=(double) (arr[i]);
        return arrDouble;
    }

    public static long getDifferenceDays(String d1, String year_sel) {
        String d2 = "01/06/"+ year_sel;

        long diff = 0;
        try {
            diff = ( new SimpleDateFormat("dd/MM/yyyy").parse(d1).getTime() - new SimpleDateFormat("dd/MM/yyyy").parse(d2).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}


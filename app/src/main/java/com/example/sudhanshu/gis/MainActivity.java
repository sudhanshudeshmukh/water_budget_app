package com.example.sudhanshu.gis;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.weiwang.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;


    String soiltype[] = {"Clay Loam", "Clayey", "Gravelly Clay", "Gravelly Clay Loam", " Gravelly Loam", "Gravelly Sandy Clay Loam ", "Gravelly Sandy Loam", "Gravelly Silty Clay", "Gravelly Silty Loam", "Loamy","Loamy Sand","Sandy","Sandy Clay", "Sandy Clay Loam", "Sandy Loam","Silty Clay", "Silty Clay Loam","Silty Loam"};
    String landuse[] = {"Agriculture", "Forest", "Fallow Land", "Habitation", "Scrub", "Waste Land", "Current Fallow"};
    String year[] = {"2013", "2014", "2015", "2016", "2017"};
    Integer irrigation[] = {1,2,3,4,5,6,7,8,9,10};
    String crop[] = {"Cauliflower", "Sorghum", "Onion", "Cotton", "Tomato", "Vegetables", "Chilly", "Sugarcane", "Bajri", "Tur", "Lemon", "Fodder Crop", "Potato", "Pomogranate", "Udid", "Banana", "Groundnut", "Soybean", "Brinjal", "Sweetlime", "small Vegetable", "Maize", "Sunflower", "Orange", "Moong", "Turmeric", "Grapes"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, soiltype);
        MaterialBetterSpinner soil = findViewById(R.id.soiltype);
        soil.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, landuse);
        MaterialBetterSpinner landuse = findViewById(R.id.landuse);
        landuse.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, year);
        MaterialBetterSpinner year = findViewById(R.id.year);
        year.setAdapter(arrayAdapter2);

        ArrayAdapter<Integer> arrayAdapter3 = new ArrayAdapter<Integer>(this, android.R.layout.simple_dropdown_item_1line, irrigation);
        final MaterialSpinner irrigation = findViewById(R.id.irrigation);
        irrigation.setAdapter(arrayAdapter3);

        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, crop);
        MaterialBetterSpinner crop = findViewById(R.id.crop);
        crop.setAdapter(arrayAdapter4);

        //Toast.makeText(MainActivity.this, "in activity", Toast.LENGTH_LONG).show();

        irrigation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recyclerView = findViewById(R.id.recyclerView);
                String item = irrigation.getItemAtPosition(position).toString();

                int n = Integer.parseInt(item);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                listItems = new ArrayList<>();
                for(int i=0; i < n;i++){
                    ListItem listItem = new ListItem(
                            "Select Date"
                    );
                    listItems.add(listItem);
                }
                adapter = new MyAdapter(listItems, MainActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(isServicesOk()){

            init();
        }

    }

    private void init(){
        Toast.makeText(MainActivity.this, "Service", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }
    public boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){

            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, 9001);
            dialog.show();
        }
        else{
             Toast.makeText(MainActivity.this, "Cant make any request", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}

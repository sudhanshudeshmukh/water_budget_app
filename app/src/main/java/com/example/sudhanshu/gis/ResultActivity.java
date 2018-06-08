package com.example.sudhanshu.gis;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    LineChart lineChart,lineChart1;
    CombinedChart combinedChart,combinedChart1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Log.d("Result Start", "Result start ");
        final ProgressDialog progressDialog = new ProgressDialog(ResultActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        kharifModel km = (kharifModel)getIntent().getSerializableExtra("serialize data");
        combinedChart = (CombinedChart) findViewById(R.id.chart);
        int crop_end_index = km.crop_end_index -1;
        String[] xaxes = new String[crop_end_index+1];
        ArrayList<Entry> aet = new ArrayList<>();
        ArrayList<Entry> pet = new ArrayList<>();
        ArrayList<Entry> sm = new ArrayList<>();
        ArrayList<Entry> runoff = new ArrayList<>();
        ArrayList<BarEntry> rainfall = new ArrayList<>();
        ArrayList<Entry> vulnerability = new ArrayList<>();
        ArrayList<Entry> rain_s = new ArrayList<>();
        ArrayList<Entry> runoff_s = new ArrayList<>();

        List<Double> pet_val = km.pet;
        List <Double> aet_val =km.aet;

        double vuln_sum=0;
        double rain_sum=0;
        double runoff_sum=0;
//        double [] vulnerability = new double[crop_end_index+1];
        for (int i =0;i<=crop_end_index;i++){
             aet.add(new Entry(i,aet_val.get(i).floatValue()));
             pet.add(new Entry(i,pet_val.get(i).floatValue()));
             sm.add(new Entry (i,(km.sm.get(i).floatValue() - (float)km.wilting_point)));
             runoff.add(new Entry (i,km.runoff.get(i).floatValue()));
             rainfall.add(new BarEntry(i, (float)km.rainfall[i]));
             xaxes[i]=Integer.toString(i);
             Log.d("Day "+i+" AET", Double.toString(aet_val.get(i)));
             Log.d("Day "+i+" PET", Double.toString(pet_val.get(i)));
             vuln_sum += pet_val.get(i) - aet_val.get(i);
             rain_sum += km.rainfall[i];
             runoff_sum+=km.runoff.get(i);
            vulnerability.add(new Entry(i,(float)vuln_sum));
            rain_s.add(new Entry(i,(float)rain_sum));
            runoff_s.add(new Entry(i, (float) runoff_sum));

        }
        progressDialog.dismiss();
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(aet,"AET");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.GREEN);
//        LineData ld1 = new LineData();
//        ld1.addDataSet(lineDataSet1);

        LineDataSet lineDataSet2 = new LineDataSet(pet,"PET");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setColor(Color.RED);
//        LineData ld2 = new LineData();
//        ld2.addDataSet(lineDataSet2);


//        LineDataSet lineDataSet3 = new LineDataSet(sm,"Soil Moisture");
//        lineDataSet3.setDrawCircles(false);
//        lineDataSet3.setColor(Color.MAGENTA);

//        LineDataSet lineDataSet4 = new LineDataSet(runoff,"Runoff");
//        lineDataSet4.setDrawCircles(false);
//        lineDataSet4.setColor(Color.CYAN);

        BarDataSet barDataSet5 = new BarDataSet(rainfall,"Rainfall");
//        barDataSet5.setDrawCircles(false);
        barDataSet5.setColor(Color.BLUE);
        BarData bd5 =new BarData();
        bd5.addDataSet(barDataSet5);




        lineDataSets.add(lineDataSet1);
        lineDataSets.add(lineDataSet2);
//        lineDataSets.add(lineDataSet3);
//        lineDataSets.add(lineDataSet4);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(new LineData(lineDataSets));
        combinedData.setData(bd5);

        combinedChart.setData(combinedData);

        combinedChart1 = (CombinedChart) findViewById(R.id.chart1);
        ArrayList<ILineDataSet> lineDataSets1 = new ArrayList<>();

        LineDataSet lineDataSet6 = new LineDataSet(vulnerability,"PET- AET");
        lineDataSet6.setDrawCircles(false);
        lineDataSet6.setColor(Color.RED);

//        LineDataSet lineDataSet7 = new LineDataSet(rain_s,"Rainfall");
//        lineDataSet7.setDrawCircles(false);
//        lineDataSet7.setColor(Color.BLUE);

        LineDataSet lineDataSet8 = new LineDataSet(runoff_s,"Runoff");
        lineDataSet8.setDrawCircles(false);
        lineDataSet8.setColor(Color.MAGENTA);


        lineDataSets1.add(lineDataSet6);
//        lineDataSets1.add(lineDataSet7);
        lineDataSets1.add(lineDataSet8);
//        lineChart1.setData(new LineData(lineDataSets1));


        CombinedData combinedData1 = new CombinedData();
        combinedData1.setData(new LineData(lineDataSets1));
        combinedData1.setData(bd5);

        combinedChart1.setData(combinedData1);

        double gwr_sum_monsoon=0,rain_sum_monsoon=0,runoff_sum_monsoon=0,defecit_sum_monsoon=0;
        for (int i=0;i<=km.monsoon_end_index;i++){
            rain_sum_monsoon+= km.rainfall[i];
            runoff_sum_monsoon+= km.runoff.get(i);
            defecit_sum_monsoon+= km.pet.get(i) - km.aet.get(i);
            gwr_sum_monsoon += km.GW_rech.get(i);
        }


//        TextView Raintxt = (TextView) findViewById(R.id.rainfall);
//        Raintxt.setText("Rainfall till crop end: "+ (Math.round(rain_sum))+ " mm");
//        TextView runofftxt = (TextView) findViewById(R.id.runoff);
//        runofftxt.setText("Runoff till crop end  "+ (Math.round(runoff_sum))+ " mm");
//        TextView Defecittxt = (TextView) findViewById(R.id.defecit);
//        Defecittxt.setText("Total Defecit"+ (Math.round(vuln_sum))+ " mm");
//        TextView gwrtxt = (TextView) findViewById(R.id.gwr);
//        gwrtxt.setText("GW Recarge: "+ (Math.round((km.sm.get(crop_end_index) - km.wilting_point)*100)/100)+ " mm");

        TextView Raintxt = (TextView) findViewById(R.id.rainfall);
        Raintxt.setText("Rainfall till Monsoon end: "+ (Math.round(rain_sum_monsoon))+ " mm");
        TextView runofftxt = (TextView) findViewById(R.id.runoff);
        runofftxt.setText("Runoff till Monsoon end: "+ (Math.round(runoff_sum_monsoon))+ " mm");
        TextView Defecittxt = (TextView) findViewById(R.id.defecit);
        Defecittxt.setText("Total Defecit in Monsoon: "+ (Math.round(defecit_sum_monsoon))+ " mm");
        TextView gwrtxt = (TextView) findViewById(R.id.gwr);
        gwrtxt.setText("GW Recarge in Monsoon: "+ (Math.round(gwr_sum_monsoon)+ " mm"));


        Log.d("Result Finish", "Result fin ");



    }

    public static double[] makeCumul(double[] in) {
        double[] out = new double[in.length];
        double total = 0;
        for (int i = 0; i < in.length; i++) {
            total += in[i];
            out[i] = total;
        }
        return out;
    }

}

package com.example.sudhanshu.gis;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

    public class kharifModel implements Serializable {
    double depth_value;
    double ksat;
    double sat;
    double wilting_point;
    double field_capacity;
    double sm1;
    double sm2;
    double cn;
    String lulc;
    String soil_type;
    double slope;
    String crop;
    double layer2_moisture;
    double sm1_fraction;
    String hsg;
    double WP_depth;
    double Smax;
    double w2;
    double w1;
    double daily_perc_factor;
    double depletion_factor;
    double sm1_before;
    double sm2_before;
    double r_to_second_layer;
    int crop_end_index;
    double [] rainfall;
    Lookup l;
    List <Double> sm,runoff,infil,aet,sec_run_off,GW_rech,pet;
    int monsoon_end_index;

    kharifModel(String lulc, String soil_type, double depth, double slope, String crop,double [] rf, int monsoon_end_index){
        l = new Lookup();
        depth_value=depth;
        this.slope = slope;
        this.crop=crop;
//        Log.d("Soil type km", soil_type);
        ksat=l.soil.get(soil_type).ksat;
        wilting_point = l.soil.get(soil_type).wilting_point;
        field_capacity = l .soil.get(soil_type).field_capacity;
        sat = l.soil.get(soil_type).saturation;
        sm1_fraction=layer2_moisture=wilting_point;
        this.lulc =lulc;
        this.soil_type=soil_type;
        hsg = l.soil.get(soil_type).HSG;
        sm = new ArrayList <Double>();
        runoff = new ArrayList <Double>();
        infil = new ArrayList <Double>();
        aet = new ArrayList <Double>();
        sec_run_off = new ArrayList <Double>();
        GW_rech = new ArrayList <Double>();
        pet = new ArrayList <Double>();
        rainfall = Arrays.copyOf(rf,rf.length);
        this.monsoon_end_index = monsoon_end_index;
//        Log.d("Data to Kharif",lulc+"\t"+soil_type+"\t"+Double.toString(depth)+"\t"+Double.toString(slope)+"\t"+crop);




    }


    void setup(){
        double sat_depth = sat* depth_value* 1000;
        WP_depth = wilting_point* depth_value* 1000;
        double FC_depth = field_capacity* depth_value* 1000;
        double root_depth = l.crop.get(crop).root;
        depletion_factor = l.crop.get(crop).depletion;

        if(depth_value<=root_depth){
            sm1=depth_value - 0.05;
            sm2=0.05;
        }
        else{
            sm1=root_depth;
            sm2=depth_value-root_depth;

        }
        double cn_val;
        if(hsg == "A")
            cn_val = l.cn.get(lulc).a;
        else if(hsg == "B")
            cn_val = l.cn.get(lulc).b;
        else if (hsg =="C")
            cn_val = l.cn.get(lulc).c;
        else
            cn_val = l.cn.get(lulc).d;
        // switch (hsg)
        // {
        //   case "A": cn_val = l.cn.get(l.landuse.get(lulc)).a;
        //             break;
        //   case "B": cn_val = l.cn.get(l.landuse.get(lulc)).b;
        //             break;
        //   case "C": cn_val = l.cn.get(l.landuse.get(lulc)).c;
        //             break;
        //   case "D": cn_val = l.cn.get(l.landuse.get(lulc)).d;
        //             break;
        // }


        double cn3 = cn_val * Math.exp(0.00673*(100-cn_val));
        double cn3_s,cn_s,cn1_s;
        if(slope>5.0){
            cn_s = (((cn3-cn_val)/3)*(1-2*Math.exp(-13.86*slope*0.01))) + cn_val;
        }
        else{
            cn_s = cn_val;
        }
        cn1_s = cn_s - 20*(100-cn_s)/(100-cn_s+Math.exp(2.533-0.0636*(100-cn_s)));
        cn3_s = cn_s *Math.exp(0.00673*(100-cn_s));
        Smax = 25.4 * (1000/cn1_s - 10);
        double s3 = 25.4 * (1000/cn3_s - 10);
        w2 = (Math.log((FC_depth- WP_depth)/(1- s3/Smax) - (FC_depth - WP_depth )) - Math.log ((sat_depth - WP_depth)/(1-2.54/Smax) - (sat_depth - WP_depth)))/((sat_depth- WP_depth) - (FC_depth - WP_depth));
        w1 = Math.log((FC_depth- WP_depth)/(1- s3/Smax) - (FC_depth - WP_depth)) + w2 * (FC_depth -WP_depth);
        double TT_perc = (sat_depth- FC_depth)/ ksat;
        daily_perc_factor = 1 - Math.exp(-24 / TT_perc) ;

    }

    void primary_runoff(int day,double [] rain){
        sm.add(  (sm1_fraction * sm1 + layer2_moisture * sm2) * 1000);
        double sw = sm.get(sm.size() - 1) - WP_depth;
        double S_swat = Smax *(1 - sw/(sw + Math.exp(w1 - w2 * sw)));
        double Cn_swat = 25400/(S_swat+254);
        double Ia_swat = 0.2 * S_swat;
        if(rain[day] > Ia_swat)
            runoff.add(Math.pow((rain[day]-Ia_swat),2)/(rain[day] + 0.8*S_swat));
        else
            runoff.add(0.0);

        infil.add(rain[day] - runoff.get(day));
//        Log.d("DAY "+day+" RO",Double.toString((sm1_fraction * sm1 + layer2_moisture * sm2) * 1000)+"\t"+Double.toString(Ia_swat)+"\t"+Double.toString(runoff.get(runoff.size() -1 ))+"\t"+Double.toString(infil.get(infil.size() -1 )));

    }

    void Aet (int day){
        double ks;
        if(sm1_fraction < wilting_point){
            ks=0;
        }
        else if (sm1_fraction > (field_capacity *(1- depletion_factor) + depletion_factor * wilting_point))
        {
            ks =1;
        }
        else{
            ks =  (sm1_fraction - wilting_point)/(field_capacity - wilting_point) /(1- depletion_factor);

        }
        aet.add(ks * pet.get(day) );
//        Log.d("DAY "+day+" AET",Double.toString(ks)+"\t"+Double.toString(ks+pet.get(day)));
    }

    void percolation_below_root_zone(int day){
        sm1_before = (sm1_fraction*sm1 +((infil.get(day) - aet.get(day))/1000))/sm1;
        if(sm1_before<field_capacity){
            r_to_second_layer =0;
        }
        else if(layer2_moisture<sat){
            r_to_second_layer = Math.min((sat - layer2_moisture) * sm2 * 1000,(sm1_before - field_capacity) * sm1 * 1000 * daily_perc_factor);
        }
        else{
            r_to_second_layer=0;
        }
        sm2_before = (layer2_moisture*sm2*1000 + r_to_second_layer)/sm2/1000;

    }

    void secondary_runoff(int day){
        if(((sm1_before*sm1 - r_to_second_layer/1000)/sm1) > sat){
            sec_run_off.add((((sm1_before*sm1 - r_to_second_layer/1000)/sm1) - sat) * sm1 * 1000);
        }
        else{
            sec_run_off.add(0.0);
        }
        sm1_fraction = Math.min((sm1_before*sm1*1000 - r_to_second_layer)/sm1/1000,sat);
    }

    void percolation_to_GW(int day){
        GW_rech.add(Math.max((sm2_before - field_capacity)*sm2*daily_perc_factor*1000,0));
        layer2_moisture = Math.min(((sm2_before*sm2*1000- GW_rech.get(GW_rech.size()-1))/sm2/1000),sat);


    }


    void pET_calculation(double [] et0, int sowing_threshold,double [] rain){
        double rain_sum=0;
        int dry_spell=0;
        for (int i=0; i<rain.length;i++){
            if(rain_sum<sowing_threshold){
                rain_sum+=rain[i];
                dry_spell++;

            }
            else
                break;
        }
        Log.d("Dry spell", Integer.toString(dry_spell));
        List<Double> kc = new ArrayList<Double>(l.crop.get(crop).list);

        for (int i=0;i<=dry_spell;i++){
            kc.add(0,0.0);
        }

        int remain = 365-kc.size();
        if(remain>0)
            crop_end_index = kc.size();
        else
            crop_end_index = 365;
        for(int k=0; k<remain ; k++){
            kc.add(0.0);
        }
        while (kc.size()>365)
            kc.remove(kc.size()-1);
        for (int i=0;i<365;i++){
            pet.add(et0[i]*kc.get(i));

        }

    }



}
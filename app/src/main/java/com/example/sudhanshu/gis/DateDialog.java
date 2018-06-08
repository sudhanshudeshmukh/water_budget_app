package com.example.sudhanshu.gis;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText txtdate;
    TextView txtview;
    int isEdittxt;
    int year;
    int month;
    int day;
    int pos;

    public DateDialog(View view, int year, int month, int day, int pos){
        if(view instanceof EditText) {
            txtdate = (EditText) view;
            isEdittxt = 1;

        }
        else if (view instanceof  TextView) {
            txtview = (TextView) view;
            isEdittxt =0;
        }
        this.year = year;
        this.month=month;
        this.day=day;
        this.pos = pos;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
//        int year = year;
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        String minDate = "01/06/"+Integer.toString(year);
        String maxDate = "31/05/"+Integer.toString(year+1);
//        Log.d("Min Date Str", minDate);
//        Log.d("Max Date str", maxDate);

        try {
            dialog.getDatePicker().setMinDate(new SimpleDateFormat("dd/MM/yyyy").parse(minDate).getTime());
//            Log.d("Min Date", Long.toString(new SimpleDateFormat("dd/MM/yyyy").parse(minDate).getTime()));

        }
        catch (ParseException e){
            e.printStackTrace();
        }
        try {
            dialog.getDatePicker().setMaxDate(new SimpleDateFormat("dd/MM/yyyy").parse(maxDate).getTime());

//            Log.d("Max Date", Long.toString(new SimpleDateFormat("dd/MM/yyyy").parse(maxDate).getTime()));

        }
        catch (ParseException e){
            e.printStackTrace();
        }
        // Create a new instance of DatePickerDialog and return it
        return dialog;


    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        String date=day+"/"+(month+1)+"/"+year;
        if(isEdittxt==1)
            txtdate.setText(date);
        else{
            txtview.setText("Date: "+date);
            SharedPreferences spref = getActivity().getSharedPreferences("dates", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = spref.edit();
            edit.putString("Date_"+Integer.toString(pos), date);
            edit.apply();
        }

    }




}


package com.example.converter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity {
    private int years;
    private int months;
    private int days;
    TextView TV_data;
    private Handler handler;
    private Handler handler2;
    MyTask mt;
    ProgressBar pb;
    double firstnumber;
    Spinner SP_1;
    EditText ET_num2;
    EditText ET_num1;
    Spinner SP_2;
    boolean flag = true;



    private DatePickerDialog.OnDateSetListener listener =
            new DatePickerDialog.OnDateSetListener(){
                public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (year == years && months == monthOfYear && days > dayOfMonth) {

                    }
                    else {
                        years = year;
                        months = monthOfYear;
                        days = dayOfMonth;
                        updateDisplay();
                        mt = new MyTask();
                        mt.execute();

                    }
                }
            };

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Valuta.getInstance().update(TV_data.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pb.setVisibility(View.INVISIBLE);
            if(flag) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, Valuta.getInstance().getKeys());
                ArrayAdapter<String> ad2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, Valuta.getInstance().getKeys());
                SP_1.setAdapter(adapter);
                SP_2.setAdapter(ad2);
                flag = false;
            }
                ET_num2.setText(Valuta.getInstance().convert(SP_1.getSelectedItem().toString(), SP_2.getSelectedItem().toString(), Double.valueOf(ET_num1.getText().toString())));
            }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("num1", ET_num1.getText().toString());
        outState.putString("num2", ET_num2.getText().toString());
        outState.putInt ("years", years);
        outState.putInt("months", months);
        outState.putInt("days", days);
        outState.putInt("pos1", SP_1.getSelectedItemPosition());
        outState.putInt("pos2", SP_2.getSelectedItemPosition());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
            SP_1 = (Spinner) findViewById(R.id.spinner);
            SP_2 = (Spinner) findViewById(R.id.spinner2);
            TV_data = (TextView) findViewById(R.id.textView6);
            ET_num1 = (EditText) findViewById(R.id.editText);
            ET_num2 = (EditText) findViewById(R.id.editText2);
            pb = (ProgressBar) findViewById(R.id.progressBar);
        }
        else{
            setContentView(R.layout.main_horizontal);
            SP_1 = (Spinner) findViewById(R.id.spinner3);
            SP_2 = (Spinner) findViewById(R.id.spinner4);
            TV_data = (TextView) findViewById(R.id.textView7);
            ET_num1 = (EditText) findViewById(R.id.editText3);
            ET_num2 = (EditText) findViewById(R.id.editText4);
            pb = (ProgressBar) findViewById(R.id.progressBar2);
        }
        ET_num1.setText("0");
        pb.setVisibility(View.INVISIBLE);
        ET_num1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    ET_num2.setText(Valuta.getInstance().convert(SP_1.getSelectedItem().toString(), SP_2.getSelectedItem().toString(), Double.valueOf(ET_num1.getText().toString())));
                }
            catch (Exception ex){}}

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final Calendar c = Calendar.getInstance();
        years = c.get(Calendar.YEAR);
        months = c.get(Calendar.MONTH);
        days = c.get(Calendar.DAY_OF_MONTH);
        if( savedInstanceState != null ) {
            ET_num1.setText(savedInstanceState.getString("num1"));
            ET_num2.setText(savedInstanceState.getString("num2"));
            years = savedInstanceState.getInt("years");
            months = savedInstanceState.getInt("months");
            days = savedInstanceState.getInt("days");
            SP_1.setSelection(savedInstanceState.getInt("pos1"));
            SP_2.setSelection(savedInstanceState.getInt("pos2"));
        }
        updateDisplay();
        mt = new MyTask();
        mt.execute();
        TV_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, listener, years, months, days);
                DatePicker datePicker = dialog.getDatePicker();
                Calendar calendar = Calendar.getInstance();//get the current day
                datePicker.setMaxDate(calendar.getTimeInMillis());
                dialog.show();
            }
        });

        SP_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ET_num2.setText(Valuta.getInstance().convert(SP_1.getSelectedItem().toString(), SP_2.getSelectedItem().toString(), Double.valueOf(ET_num1.getText().toString())));            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SP_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ET_num2.setText(Valuta.getInstance().convert(SP_1.getSelectedItem().toString(), SP_2.getSelectedItem().toString(), Double.valueOf(ET_num1.getText().toString())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try {
           ET_num2.setText(Valuta.getInstance().convert(SP_1.getSelectedItem().toString(), SP_2.getSelectedItem().toString(), Double.valueOf(ET_num1.getText().toString())));
        }
        catch (Exception ex)
        {
        }


    }
    String days2; String months2;
    void updateDisplay() {
        days2 = Integer.toString(days);
        months2 = Integer.toString(months+1);
        if (days < 10)
        {
            days2 = "0" + Integer.toString(days);
        }
        if (months+1 < 10)
        {
            months2 = "0" + Integer.toString(months+1);
        }
        TV_data.setText(days2 + "/" + months2 + "/" + Integer.toString(years));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
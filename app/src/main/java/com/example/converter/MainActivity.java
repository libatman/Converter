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
import android.widget.Toast;
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
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity {
    private int years;
    private int months;
    private int days;
    TextView TV_data;
    TextView TV_eur;
    TextView TV_usd;
    private Handler handler;
    private Handler handler2;
    MyTask mt;
    ProgressBar pb;
    double firstnumber;
    Spinner SP_1;
    EditText ET_num2;
    EditText ET_num1;
    Spinner SP_2;



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
                        convert();
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
                parsing();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pb.setVisibility(View.INVISIBLE);
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
            TV_eur = (TextView) findViewById(R.id.textView5);
            TV_usd = (TextView) findViewById(R.id.textView3);
            ET_num1 = (EditText) findViewById(R.id.editText);
            ET_num2 = (EditText) findViewById(R.id.editText2);
            pb = (ProgressBar) findViewById(R.id.progressBar);
        }
        else{
            setContentView(R.layout.main_horizontal);
            SP_1 = (Spinner) findViewById(R.id.spinner3);
            SP_2 = (Spinner) findViewById(R.id.spinner4);
            TV_data = (TextView) findViewById(R.id.textView7);
            TV_eur = (TextView) findViewById(R.id.textView12);
            TV_usd = (TextView) findViewById(R.id.textView10);
            ET_num1 = (EditText) findViewById(R.id.editText3);
            ET_num2 = (EditText) findViewById(R.id.editText4);
            pb = (ProgressBar) findViewById(R.id.progressBar2);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.MONEY_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> ad2 = ArrayAdapter.createFromResource(this, R.array.MONEY_array, android.R.layout.simple_spinner_item);
        SP_1.setAdapter(adapter);
        SP_2.setAdapter(ad2);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                TV_usd.setText(text);
            }
        };
        handler2 = new Handler() {
            @Override
            public void handleMessage (Message msg) {
                String text = (String) msg.obj;
                TV_eur.setText(text);
            }
        };

        pb.setVisibility(View.INVISIBLE);
        ET_num1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                convert();
            }

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
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Пора покормить кота!", Toast.LENGTH_SHORT);
            toast.show();
        }
        updateDisplay();
        mt = new MyTask();
        mt.execute();
        Valuta.getInstance().setvaluta(TV_usd.getText().toString(), TV_eur.getText().toString());
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
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SP_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




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





    @TargetApi(Build.VERSION_CODES.FROYO)
    void parsing(){
        try {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + TV_data.getText().toString());
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("Valute");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node element = nodes.item(i);
                NamedNodeMap namedNodeMap = element.getAttributes();
                if (namedNodeMap.getNamedItem("ID").getNodeValue().equals("R01235")) {
                    NodeList childs = element.getChildNodes();
                    Node node_child = childs.item(9);
                    Message msg = new Message();
                    msg.obj = node_child.getTextContent();
                    handler.sendMessage(msg);
                }
                if (namedNodeMap.getNamedItem("ID").getNodeValue().equals("R01239")) {
                    NodeList childs = element.getChildNodes();
                    Node node_child = childs.item(9);
                    Message msg = new Message();
                    msg.obj = node_child.getTextContent();
                    handler2.sendMessage(msg);
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void convert() {
        try {
            int id = SP_1.getSelectedItemPosition();
            int id2 = SP_2.getSelectedItemPosition();
            double kursevro = parsedouble(TV_eur.getText().toString());
            double kursusd = parsedouble(TV_usd.getText().toString());
            firstnumber = Double.valueOf(ET_num1.getText().toString());
            switch (id) {
                case 0:
                    switch (id2) {
                        case 0:
                            ET_num2.setText(ET_num1.getText().toString());
                            break;
                        case 1:
                            ET_num2.setText(Double.toString(new BigDecimal(firstnumber / kursevro).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                            break;
                        case 2:
                            ET_num2.setText(Double.toString(new BigDecimal(firstnumber / kursusd).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                            break;
                    }
                    break;
                case 1:
                    switch (id2) {
                        case 0:
                            ET_num2.setText(Double.toString(new BigDecimal(firstnumber * kursevro).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                            break;
                        case 1:
                            ET_num2.setText(ET_num1.getText().toString());
                            break;
                        case 2:
                            ET_num2.setText(Double.toString(new BigDecimal(kursevro / kursusd * firstnumber).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                            break;
                    }
                    break;
                case 2:
                    switch (id2) {
                        case 0:
                            ET_num2.setText(Double.toString(new BigDecimal(firstnumber * kursusd).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                            break;
                        case 1:
                            ET_num2.setText(Double.toString(new BigDecimal(kursusd / kursevro * firstnumber).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                            break;
                        case 2:
                            ET_num2.setText(ET_num1.getText().toString());
                    }
                    break;
            }
        }
        catch (Exception ex)
        {
            ET_num2.setText("");
        }
    }

    double parsedouble(String s){
        double kurs = 0;
        s = s.replace(",", ".");
        try
        {
            kurs = Double.parseDouble(s);
        }catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
        return kurs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
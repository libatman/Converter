package com.example.converter;

import android.os.Message;
import android.widget.ArrayAdapter;

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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
    public class Valuta {
        private static final Valuta instance = new Valuta();

        public Hashtable<String, Double> rates = new Hashtable<>();


        public static Valuta getInstance() {
            return instance;
        }

        public void update(String date) {
            try {
                CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date);
                URLConnection conn = url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(conn.getInputStream());
                doc.getDocumentElement().normalize();

                NodeList nodes = doc.getElementsByTagName("Valute");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node element = nodes.item(i);
                    NodeList childNodes = element.getChildNodes();

                    String charCode = null;
                    Double rate = null;

                    for (int j = 0; j < childNodes.getLength(); j++)
                    {
                        Node valuteElement = childNodes.item(j);

                        if (valuteElement.getNodeName().equals("CharCode")) {
                            charCode = valuteElement.getTextContent();
                        }
                        else if (valuteElement.getNodeName().equals("Value")) {
                            String elementValue = valuteElement.getTextContent().replace(',', '.');
                            rate = Double.valueOf(elementValue);
                        }
                    }

                    rates.put(charCode, rate);
                }
                rates.put("RUB", 1.0);

            } catch (Exception exception) {

            }
        }

        public String[] getKeys(){
            ArrayList<String> array = Collections.list(rates.keys());
            try{
                return array.toArray(new String[array.size()]);
            }
            catch(Exception e){return null;}
        }



        public String convert(String charCode1, String charCode2, Double firstnumber){
            try {
                double kurs1spinner = rates.get(charCode1);
                double kurs2spinner = rates.get(charCode2);
                String secondnumber = String.valueOf(new BigDecimal(kurs2spinner / kurs1spinner * firstnumber).setScale(4, RoundingMode.HALF_UP).doubleValue());
                return secondnumber;
            }
            catch (Exception ex)
            {
                return null;
            }
        }


        private Valuta() {

        }

}

package com.example.converter;

/**
 * Created by Елизавета on 17.04.2015.
 */
public class Valuta {
    private Valuta() {

    }
    private  static final Valuta instance = new Valuta();

    public static Valuta getInstance() {
        return instance;
    }

    private String euro = "";
    private String usd = "";

    public void setvaluta (String e, String u) {
        euro = e;
        usd = u;
    }

}

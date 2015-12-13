package com.future.jonassen.pumpcontroller;

import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Jonassen on 15/11/19.
 */
public class Bean
{
    Bean()
    {
        this.id = 0;
        this.sFlux = "a";
        this.way = false;
        this.hasConvert = false;
    }

    Bean(Bean bean) {
        this.id = bean.id;
        this.sFlux = bean.sFlux;
        this.way = bean.way;
        this.hasConvert = bean.hasConvert;
    }

    Bean(int id)
    {
        this.id = id;
    }

    Bean(int id, String sFlux, boolean way)
    {
        this.id = id;
        this.sFlux = sFlux;
        this.way = way;
        this.hasConvert = false;
    }

    private int convertStringToInt(String str)
    {
        int u = 0;

        u = Integer.parseInt(str);
        hasConvert = true;
        return u;
    }

    public int getsFlux(String str)
    {
        return convertStringToInt(sFlux);
    }

    public int getFlux()
    {
        if (hasConvert == true)
        {
            return iFlux;
        }
        else
        {
            return getsFlux(sFlux);
        }
    }

    public String OutputMessage() {
        String str = new String();

        str = String.format("id = %d, sflux = %s, way = %s, iflux = %d", id, sFlux, Boolean.valueOf(way), getFlux());

        return str;
    }
    int id;
    String sFlux;
    boolean way;
    int iFlux;
    boolean hasConvert;
}

package com.future.jonassen.pump;

import android.util.Log;

/**
 * Created by Jonassen on 15/12/14.
 */
public class Flux
{

    public String sFluxML;
    public String sFluxUL;
    public int iFluxML;
    public int iFluxUL;

    Flux()
    {
        this.sFluxML = "2";
        this.sFluxUL = "500";
        this.convertVal();
    }

    Flux(Flux flux)
    {
        this.sFluxML = flux.sFluxML;
        this.sFluxUL = flux.sFluxUL;
        this.iFluxML = flux.iFluxML;
        this.iFluxUL = flux.iFluxUL;
    }



    Flux(int iML, int iUL)
    {
        this.iFluxML = iML;
        this.iFluxUL = iUL;
    }

    Flux(String sML, String sUL)
    {
        this.sFluxML = sML;
        this.sFluxUL = sUL;
    }

    public void convertVal()
    {
        iFluxML = Integer.parseInt(sFluxML);
        iFluxUL = Integer.parseInt(sFluxUL);
    }

    public void outputMessage()
    {
        Log.i("Flux", String.format("sFluxML = %sML, sFluxUL = %sUL, iFluxML = %dML, iFluxUL = %dUL", sFluxML, sFluxUL, iFluxML, iFluxUL));
    }

}

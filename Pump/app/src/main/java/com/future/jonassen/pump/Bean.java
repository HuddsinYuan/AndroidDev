package com.future.jonassen.pump;

/**
 * Created by Jonassen on 15/11/19.
 */
public class Bean
{
    Bean()
    {
        this.id = 0;
        this.way = false;
        this.flux = new Flux();
        this.flux.iFluxUL = 0;
        this.flux.iFluxML = 0;
    }

    Bean(Bean bean)
    {
        this.id = bean.id;
        this.way = bean.way;
        this.flux = bean.flux;
    }

    Bean(int id)
    {
        this.id = id;
    }

    Bean(int id, Flux flux, boolean way)
    {
        this.id = id;
        this.way = way;
        this.flux = flux;
    }


    public String OutputMessage()
    {
        String str = new String();

        str = String.format("id = %d, way = %s, flux = %dml + %dul", id,
                            String.valueOf(way), flux.iFluxML, flux.iFluxUL);

        return str;
    }

    int id;
    boolean way;
    Flux flux;
}



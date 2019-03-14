package com.example.stockwatch;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable, Comparable<Stock>
{
    private String symbol;
    private String companyName;
    private double price;
    private double priceChange;
    private double percentageChange;

    public Stock()
    {

    }

    public Stock(String symbol, String companyName, double price, double priceChange, double percentageChange)
    {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.priceChange = priceChange;
        this.percentageChange = percentageChange;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    @Override
    public int hashCode()
    {
        return 1;
    }



    @Override
    public String toString()
    {
        String str = this.symbol + " " + this.companyName;
        return str;
    }

    @Override
    public int compareTo(@NonNull Stock o)
    {
        return o.getSymbol().toLowerCase().compareTo(getSymbol().toLowerCase());
    }

    @Override
    public boolean equals(Object obj)
    {
        Stock st = (Stock) obj;
        if(this == obj)
            return true;
        if(! (obj instanceof Stock))
            return false;

        return this.getSymbol().equalsIgnoreCase(st.getSymbol());
    }
}

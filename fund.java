package com.example.geektrust;

import java.util.List;

public class Fund {
   
    private String name;
    private List<String> stocks;
    public Fund(String name, List<String> stocks) {
        this.name = name;
        this.stocks = stocks;
    }
    public Fund(){
        
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getStocks() {
        return stocks;
    }
    public void setStocks(List<String> stocks) {
        this.stocks = stocks;
    }
   
   // Need add stock method.
   // Create method variables as final
    
}

package com.example.geektrust;

import java.util.List;

public class userPortfolio  {

    // Classname should start with capital letter
    // always use `this.`
    // Funds -> funds
    // Add funds method.
    // Need method to add a new fund.
    private List<Fund> Funds;

    public userPortfolio(List<Fund> funds) {
        Funds = funds;
    }

    public List<Fund> getFunds() {
        return Funds;
    }

    public void setFunds(List<Fund> funds) {
        Funds = funds;
    }
    public userPortfolio(){
        
    }
}


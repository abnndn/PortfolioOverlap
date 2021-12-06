package com.example.geektrust;

import java.util.List;

public class userPortfolio  {

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


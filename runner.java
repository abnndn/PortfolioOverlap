package com.example.geektrust;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.json.JSONException;


public class Runner {
    // Always create variables final when you don't expect to update.
    public  String URL = "https://geektrust.s3.ap-southeast-1.amazonaws.com/portfolio-overlap/stock_data.json";
    public  String NOT_FOUND = "FUND_NOT_FOUND";

    public  int getFundNo(String fund_name, List<Fund> funds ){
        int itr=0;
        for(Fund fund:funds){
            if(fund.getName().equals(fund_name)){
                return itr;
            }
            itr++;
        }
        return -1;
    }

    public String getStockName(List<String> instructions){
        int itr = 0;
        String stock_name = "";
        for(String word: instructions){
            if(itr==0){
                itr++;
            }
            else{
                stock_name += word;
                stock_name += " ";
            }
        }
        //Removing unwanted spaces on either side
        stock_name = stock_name.trim();
        return stock_name;
    }
    
    public List<String> removeAction(String[] instructions){
        List<String> names = new ArrayList<>();
        int itr=0;
        for(String instruction: instructions){
            if(itr == 0){
                itr++;
                continue;
            }
            else    
                names.add(instruction);
        }
        return names;
    }

    public  List<Fund> getUserFunds(List<String> instruction, List<Fund> allFunds){
        List<Fund> userFunds = new ArrayList<>();
        for(String word:instruction){
            int index = getFundNo(word, allFunds);
            if(  index != -1){
                userFunds.add(allFunds.get(index));
            }
        }
        return userFunds;
    }

    public  List<Fund> getAllFunds(String URL) throws JsonParseException, JsonMappingException, IOException, JSONException{
        JsonHandler jsonHandler = new JsonHandler();
        String allFundsJson = jsonHandler.getJSON(URL);
        List<Fund> allFunds = jsonHandler.StringToListConversion(allFundsJson);
        return allFunds;
    }

    public  List<Fund> updateUserPortfolio(String fund_name, String stock_name, List<Fund> userFunds){
        int index = getFundNo(fund_name, userFunds);
        if(index != -1){
            userFunds.get(index).getStocks().add(stock_name);
        }
        else{
            System.out.println(NOT_FOUND);
        }
        return userFunds;
    }

    public int getCommonStocksNumber(List<String> fund_stocks, List<String> userFundStocks){
        int common_stocks = fund_stocks.stream()
            .filter(userFundStocks::contains)
            .collect(Collectors
                         .toList()).size();
        return common_stocks;
    }

    public double calculateOverlapPercentage(int common_stocks, int userStockSize, int fundStockSize){
        double overlap_percentage = 2.0*common_stocks/(userStockSize + fundStockSize);
        overlap_percentage *= 100;

        //Setting the precision to 2
        double round_off_overlap_percentage = Math.round(overlap_percentage * 100.0) / 100.0;

        return round_off_overlap_percentage;
    }

    public void printOverlap(Fund givenFund, List<Fund> userFunds){
        String fund_name = givenFund.getName();
        List<String> fund_stocks = givenFund.getStocks();
        for(Fund fund:userFunds){
            int common_stocks = getCommonStocksNumber(fund_stocks, fund.getStocks());
            double overlap_percentage = calculateOverlapPercentage(common_stocks, fund.getStocks().size(),
             fund_stocks.size());

            // ignoring the print when overlap percentage is 0.00%
            if( overlap_percentage != 0.00){
                System.out.println(fund_name + " " + fund.getName() + " " + String.format("%.2f", overlap_percentage)+"%");
            }
        }
    }

    public  void findOverlap(String fund_name, List<Fund> userFunds, List<Fund> allFunds){
        List<String> fund_stocks =  new ArrayList<>();
        
        int index = getFundNo(fund_name, allFunds);
        
        //Getting all the stocks in the fund to calculate the overlap with funds in user portfolio.
        if(index != -1){
            fund_stocks = allFunds.get(index).getStocks();
        }
        else{
            //Checking whether the fund_name is a valid and available fund.
            System.out.println(NOT_FOUND);
        }
        Fund fund = new Fund(fund_name, fund_stocks);
        printOverlap(fund, userFunds);
        
    }

    public void performActions(List<String> lines) throws JsonParseException, JsonMappingException, IOException, JSONException{
        //Getting all funds present
        List<Fund> allFunds = getAllFunds(URL);
        
        //initialising userPortfolio
        userPortfolio userPortfolio = new userPortfolio();

        for(String line:lines){
            String[] instruction = line.split(" ");
            String action = instruction[0];
            List<String> instructions = removeAction(instruction);
            if(action.equals("CURRENT_PORTFOLIO")){
                
                //Getting all the funds to be in user portfolio
                List<Fund> Funds = getUserFunds(instructions, allFunds);

                //Allocating the Funds to user portfolio
                userPortfolio.setFunds(Funds);
            }

            else if(action.equals("CALCULATE_OVERLAP")){
                String fund_name = instructions.get(0);
                findOverlap(fund_name, userPortfolio.getFunds(), allFunds);
            }

            else{
                
                String fund_name = instructions.get(0);
                String stock_name = getStockName(instructions);

                // Adding stock to the funds in user portfolio
                List<Fund> funds = updateUserPortfolio(fund_name,stock_name,userPortfolio.getFunds());
                userPortfolio.setFunds(funds);
            }
        }
    }

    public List<String> getData(String inputFileLocation) throws IOException{
        Path path = Paths.get(inputFileLocation);
        List<String> lines = Files.readAllLines(path);

        // Data fetched from the provided text input filePath.
        return lines;
    }

    public static void main(String[] args) throws IOException, JSONException  {
        Runner runner = new Runner();
        String file_location = args[0];
        //Getting the input commands from the given input file.
        List<String> lines  = runner.getData(file_location);

        // Performing the actions line by line.
        runner.performActions(lines);
    }
}


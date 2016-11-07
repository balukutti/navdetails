/*
 *  Copyright (c) 2009-2013 Novo Dia Group Inc. All rights reserved.
 *  http://novodiagroup.com
 */
package com.nav.details;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.mysql.jdbc.PreparedStatement;

/**
 * @author brajendran
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NavWebService
{

  Gson gson;
  boolean isSSL = true;
  static final String sourceUrl = "http://portal.amfiindia.com/spages/NAV0.txt";
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/navdetails";

  static final String USER = "root";
  static final String PASS = "root";

  public NavWebService(Gson gson)
  {
    this.gson = gson;
  }

  private  static   Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI("postgres://pdocuroxmivemd:5D82vwDcOvmvkdI9A6ngUXBfWH@ec2-23-23-176-135.compute-1.amazonaws.com:5432/d9ijbh8k4tf33o");

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
    dbUrl = dbUrl+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

    return DriverManager.getConnection(dbUrl, username, password);
}
  public  static Map<String, FundBasicDetails> populateFundMap(String fundCode, String line, Map<String, FundBasicDetails> resultMap, String amountInvested)
  {
    String[] arr = line.split(";");
    FundBasicDetails fundBasicDetails = new FundBasicDetails();
    fundBasicDetails.setSchemeCode(arr[0]);
    fundBasicDetails.setIsin(arr[1]);
    fundBasicDetails.setSchemeName(arr[3]);
    fundBasicDetails.setNav(arr[4]);
    fundBasicDetails.setDate(arr[7]);
    if (amountInvested != null)
    {
      fundBasicDetails.setAmountInvested(amountInvested);
    }
    resultMap.put(fundCode, fundBasicDetails);
    return resultMap;
  }

  public static void updateDatabaseWithInvestmentDetails(Map<String, FundBasicDetails> map)
  {
    Connection conn = null;
    try{
       //Class.forName("com.mysql.jdbc.Driver");
       Class.forName("org.postgresql.Driver");
       conn = getConnection();
       java.sql.Statement stmt = null;
       for (String key : map.keySet())
       {
         FundBasicDetails fundBasicDetails = new FundBasicDetails();
         fundBasicDetails = map.get(key);
         double units_held = 0;
         double amount_invested = 0;
         stmt = conn.createStatement();
         String sql = "SELECT * from navdetails where scheme_code = "+Integer.parseInt(key);
         ResultSet rs = stmt.executeQuery(sql);
         while(rs.next()){
           units_held = rs.getDouble("units_held");
           amount_invested = rs.getDouble("amount_invested");
         }
         rs.close();
         
         double unitsPurchased = Double.valueOf(fundBasicDetails.getAmountInvested()) / Double.valueOf(fundBasicDetails.getNav());
         units_held = units_held + unitsPurchased;
         amount_invested = amount_invested + Double.valueOf(fundBasicDetails.getAmountInvested());  
         double currentValue = units_held * Double.parseDouble(fundBasicDetails.getNav());
         
         String updateTableSQL = "update navdetails set units_held = "+String.valueOf(units_held)+" , amount_invested = "+ String.valueOf(amount_invested)+ " , current_value = "+String.valueOf(currentValue)+
              " where scheme_code = "+key;
         stmt.execute(updateTableSQL);
       }
       conn.close();
    }
    catch(SQLException se)
    {
       se.printStackTrace();
    }
    catch(Exception e)
    {
       e.printStackTrace();
    }
  }
  
  
  
  public static void updateDatabaseWithLatestData(Map<String, FundBasicDetails> map)
  {
    Connection conn = null;
    try{
      Class.forName("org.postgresql.Driver");
      conn = getConnection();
       java.sql.Statement stmt = null;
       for (String key : map.keySet())
       {
         FundBasicDetails fundBasicDetails = new FundBasicDetails();
         fundBasicDetails = map.get(key);
         double units_held = 0;
         stmt = conn.createStatement();
         String sql = "SELECT * from navdetails where scheme_code = "+Integer.parseInt(key);
         ResultSet rs = stmt.executeQuery(sql);
         while(rs.next()){
           units_held = rs.getDouble("units_held");
         }
         rs.close();
         
         double currentValue = units_held * Double.parseDouble(fundBasicDetails.getNav());
         
         SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
         SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         Date date = format1.parse(fundBasicDetails.getDate());
         String currentDate = format2.format(date);
         
         String updateTableSQL = "update navdetails set current_value = "+String.valueOf(currentValue)+" , nav = "+fundBasicDetails.getNav()+
             " , date = '" +currentDate+ "' where scheme_code = "+key;
         stmt.execute(updateTableSQL);
       }
       conn.close();
    }
    catch(SQLException se)
    {
       se.printStackTrace();
    }
    catch(Exception e)
    {
       e.printStackTrace();
    }
  }
  
  public  static void updateAllHoldingIsins(ArrayList<String> fundCodes)
  {
      Map<String, FundBasicDetails> resultMap = new HashMap<String, FundBasicDetails>();
      try
      {
      URL website = new URL(sourceUrl);
      URLConnection connection = website.openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String inputLine;
      while ((inputLine = reader.readLine()) != null)
        for(int i =0; i < fundCodes.size(); i++)
        {
            if(inputLine.contains(fundCodes.get(i)))
            {
              resultMap = populateFundMap(fundCodes.get(i), inputLine, resultMap, null);
            }
        }
      reader.close();
      // After we got the data in the map, we have to update the DB with latest data
      updateDatabaseWithLatestData(resultMap);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
  }

  public  static void updateAllInvestments(ArrayList<InvestmentDetails> fundCodes)
  {
      Map<String, FundBasicDetails> resultMap = new HashMap<String, FundBasicDetails>();
      try
      {
      URL website = new URL(sourceUrl);
      URLConnection connection = website.openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String inputLine;
      if (fundCodes != null && fundCodes.size() > 0)
      {
        while ((inputLine = reader.readLine()) != null)
        {
          for(InvestmentDetails investmentDetails: fundCodes)
          {
            if(inputLine.contains(investmentDetails.getSchemeCode()))
            {
              resultMap = populateFundMap(investmentDetails.getSchemeCode(), inputLine, resultMap, String.valueOf(investmentDetails.getInvestmentAmount()));
            }
          }
        }
      }
      reader.close();
      // After we got the data in the map, we have to update the DB with latest data
      updateDatabaseWithInvestmentDetails(resultMap);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
  }

  
  public static void main(String args[])
  {
    ArrayList<String> fundCodes = new ArrayList<String>();
    fundCodes.add("112323");
    fundCodes.add("112092");
    fundCodes.add("105989");
    fundCodes.add("118191");
    fundCodes.add("102594");
    fundCodes.add("108466");
    fundCodes.add("103504");
    fundCodes.add("105758");
    fundCodes.add("113177");
    fundCodes.add("103174");
    fundCodes.add("118102");
    fundCodes.add("103360");
    fundCodes.add("112932");
    fundCodes.add("111381");
    fundCodes.add("102941");
    fundCodes.add("105817");
    fundCodes.add("100122");
    fundCodes.add("112090");
    fundCodes.add("101818");
    fundCodes.add("109445");
    updateAllHoldingIsins(fundCodes);
  }

  public void updateAllFunds(ArrayList<String> fundCodes)
  {
    updateAllHoldingIsins(fundCodes);
  }
  
  public void updateInvestments(ArrayList<InvestmentDetails> investmentDetailsList)
  {
    updateAllInvestments(investmentDetailsList);
  }

}

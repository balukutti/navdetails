package com.nav.details;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NavService {

  FundBasicDetails fundBasicDetails;
//JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/navdetails";

  //  Database credentials
  static final String USER = "ndgdev";
  static final String PASS = "ndgdev";
  DecimalFormat decim = new DecimalFormat("0.00");

  public ArrayList<FundBasicDetails> getNavList() {
    return retrieveDataFromDb();
  }
  
  public String calculateProfitLossPercentage(double marketValue, double purchasePrice)
  {
    double percentage = ((marketValue - purchasePrice) / (purchasePrice) * 100); 
    if (percentage > 0)
    {
      return "+"+String.valueOf(decim.format(percentage))+"%";
    }
    if (percentage < 0)
    {
      return String.valueOf(decim.format(percentage))+"%";
    }
    return String.valueOf(decim.format(percentage))+"%";
  }
  
  private  Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI("postgres://pdocuroxmivemd:5D82vwDcOvmvkdI9A6ngUXBfWH@ec2-23-23-176-135.compute-1.amazonaws.com:5432/d9ijbh8k4tf33o");

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

    return DriverManager.getConnection(dbUrl, username, password);
}

  public ArrayList<FundBasicDetails> retrieveDataFromDb()
  {
    Connection conn = null;
    Statement stmt = null;
    ArrayList<FundBasicDetails> fundList = new ArrayList<FundBasicDetails>();
    SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yy");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    try{
       Class.forName("com.mysql.jdbc.Driver");
       String investedAmount = null;
       String currentValue = null;
       conn = getConnection();
       //conn = DriverManager.getConnection(DB_URL,USER,PASS);
       stmt = conn.createStatement();
       String sqlAmounts = "select sum(amount_invested) as 'invested' , sum(current_value) as 'valuation' from navdetails";
       String sql = "SELECT * from navdetails";
       ResultSet rsAmounts = stmt.executeQuery(sqlAmounts);
       while(rsAmounts.next())
       {
         investedAmount = String.valueOf(decim.format(rsAmounts.getDouble("invested")));
         currentValue = String.valueOf(decim.format(rsAmounts.getDouble("valuation")));
       }
       ResultSet rs = stmt.executeQuery(sql);
       while(rs.next()){
         FundBasicDetails fundBasicDetails = new FundBasicDetails();
         fundBasicDetails.setSchemeCode(rs.getString("scheme_code"));
         fundBasicDetails.setSchemeName(rs.getString("scheme_name"));
         fundBasicDetails.setIsin(rs.getString("isin"));
         fundBasicDetails.setNav(String.valueOf(decim.format(rs.getDouble("nav"))));
         Date date = format2.parse(rs.getString("date"));
         String currentDate = format1.format(date);
         fundBasicDetails.setDate(currentDate);
         fundBasicDetails.setCustomSchemeName(rs.getString("custom_scheme_name"));
         fundBasicDetails.setAmountInvested(String.valueOf(decim.format(rs.getDouble("amount_invested"))));
         fundBasicDetails.setCurrentAmount(String.valueOf(decim.format(rs.getDouble("current_value"))));
         fundBasicDetails.setProfitLossPercentage(calculateProfitLossPercentage(rs.getDouble("current_value"), rs.getDouble("amount_invested")));
         fundBasicDetails.setTotalCurrentAmount(currentValue);
         fundBasicDetails.setTotalInvestedAmount(investedAmount);
         fundList.add(fundBasicDetails);
       }
       rs.close();
       conn.close();
    }catch(SQLException se){
       se.printStackTrace();
    }catch(Exception e){
       e.printStackTrace();
    }
    return fundList;
  }
}
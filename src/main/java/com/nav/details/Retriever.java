package com.nav.details;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;


public class Retriever extends Thread
{

  protected Timer _shipStationScheduledTimer;
  protected TimerTask _syncShipStationDataTask;
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/navdetails";

  static final String USER = "root";
  static final String PASS = "root";
  HashMap<Integer, String> monthsMapping = null;

  public Retriever()
  {
    monthsMapping = new HashMap<Integer, String>();
    monthsMapping.put(1, "january");
    monthsMapping.put(2, "february");
    monthsMapping.put(3, "march");
    monthsMapping.put(4, "april");
    monthsMapping.put(5, "may");
    monthsMapping.put(6, "june");
    monthsMapping.put(7, "july");
    monthsMapping.put(8, "august");
    monthsMapping.put(9, "september");
    monthsMapping.put(10, "october");
    monthsMapping.put(11, "november");
    monthsMapping.put(12, "december");
  }
    
  public synchronized void scheduleSyncShipStationDataTask()
  {
    _shipStationScheduledTimer = new Timer();
    _syncShipStationDataTask = new SyncShipStationDataTask();
    Date nextExecutionTime = setTimeToRunNext(new Date());
    System.out.println("Scheduler is set to run at :"+nextExecutionTime);
    _shipStationScheduledTimer.schedule(_syncShipStationDataTask, nextExecutionTime);
  }
  
  public Date setTimeToRunNext(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    
    calendar.add(Calendar.DATE, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 1);
    calendar.set(Calendar.MINUTE, 00);
    calendar.set(Calendar.SECOND, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  // We need to run this update daily at evening 5:00 pm
  public synchronized void updateNavsOfAllFunds()
  {
    Connection conn = null;
    Statement stmt = null;
    ArrayList<String> isinCodes = new ArrayList<String>();
    try
    {
      Class.forName("org.postgresql.Driver");
      conn = getConnection();
      stmt = conn.createStatement();
      String queryToSelectAllIsin = "SELECT scheme_code from navdetails";
      ResultSet resultSet = stmt.executeQuery(queryToSelectAllIsin);
      while(resultSet.next())
      {
        isinCodes.add(resultSet.getString("scheme_code"));
      }
      NavWebService webService = new NavWebService(new Gson());
      webService.updateAllFunds(isinCodes);
    }
    catch (Exception e)
    {
      this._shipStationScheduledTimer.cancel();
      scheduleSyncShipStationDataTask();
    }
  }
  
  
//We need to run this update daily at evening 5:00 pm
 public synchronized void updateInvestmentDetails()
 {
   Connection conn = null;
   Statement stmt = null;
   String date = getDayOfMonth();
   int month = getMonth();
   ArrayList<InvestmentDetails> investmentDetailsList = new ArrayList<InvestmentDetails>();
   try
   {
     Class.forName("org.postgresql.Driver");
     conn = getConnection();
     stmt = conn.createStatement();
     String queryToSelectInvestmentOnToday = "SELECT * from investment_schedule where "  +monthsMapping.get(month)+  " = " +date;
     ResultSet resultSet = stmt.executeQuery(queryToSelectInvestmentOnToday);
     while(resultSet.next())
     {
       InvestmentDetails investmentDetails = new InvestmentDetails();
       investmentDetails.setSchemeCode(resultSet.getString("scheme_code"));
       investmentDetails.setDay(resultSet.getString("day"));
       investmentDetails.setInvestmentAmount(Double.parseDouble(resultSet.getString("investment_amount")));
       investmentDetails.setBatch(resultSet.getString("batch"));
       investmentDetailsList.add(investmentDetails);
     }
     
     if (investmentDetailsList.size() > 0)
     {
       NavWebService webService = new NavWebService(new Gson());
       webService.updateInvestments(investmentDetailsList);
     }
     this._shipStationScheduledTimer.cancel();
     scheduleSyncShipStationDataTask();
   }
   catch (Exception e)
   {
     this._shipStationScheduledTimer.cancel();
     scheduleSyncShipStationDataTask();
   }
 }

 private  Connection getConnection() throws URISyntaxException, SQLException {
   URI dbUri = new URI("postgres://pdocuroxmivemd:5D82vwDcOvmvkdI9A6ngUXBfWH@ec2-23-23-176-135.compute-1.amazonaws.com:5432/d9ijbh8k4tf33o");

   String username = dbUri.getUserInfo().split(":")[0];
   String password = dbUri.getUserInfo().split(":")[1];
   String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
   dbUrl = dbUrl+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

   return DriverManager.getConnection(dbUrl, username, password);
}
 
 public String getDayOfMonth()
 {
   Calendar cal = Calendar.getInstance();
   cal.setTime(new Date());
   return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
 }
 
  public int getMonth()
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    return cal.get(Calendar.MONTH) + 1;
  }

 
  class SyncShipStationDataTask extends TimerTask
  {
    @Override
    public void run()
    {
      updateNavsOfAllFunds();
      updateInvestmentDetails();
    }
  }
  
}

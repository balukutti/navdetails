package com.nav.details;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.StringValueExp;

import com.google.gson.Gson;


public class Retriever extends Thread
{

  protected Timer _shipStationScheduledTimer;
  protected TimerTask _syncShipStationDataTask;
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/navdetails";

  static final String USER = "root";
  static final String PASS = "root";


  public Retriever()
  {
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
    //calendar.add(Calendar.SECOND,45);
    
    calendar.add(Calendar.DATE, 1); 
    calendar.set(Calendar.HOUR_OF_DAY, 18);
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
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);
      stmt = conn.createStatement();
      String queryToSelectAllIsin = "SELECT isin from navdetails";
      ResultSet resultSet = stmt.executeQuery(queryToSelectAllIsin);
      while(resultSet.next())
      {
        isinCodes.add(resultSet.getString("isin"));
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
   ArrayList<InvestmentDetails> investmentDetailsList = new ArrayList<InvestmentDetails>();
   try
   {
     Class.forName("com.mysql.jdbc.Driver");
     conn = DriverManager.getConnection(DB_URL,USER,PASS);
     stmt = conn.createStatement();
     String queryToSelectInvestmentOnToday = "SELECT * from investment_schedule where day = "+date;
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

 public String getDayOfMonth()
 {
   Calendar cal = Calendar.getInstance();
   cal.setTime(new Date());
   return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
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

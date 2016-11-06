package com.nav.details;

public class InvestmentDetails
{
  private String schemeCode;
  private String batch;
  private double investmentAmount;
  private String day;
  
  /**
   * @return the schemeCode
   */
  public String getSchemeCode()
  {
    return schemeCode;
  }
  /**
   * @param schemeCode the schemeCode to set
   */
  public void setSchemeCode(String schemeCode)
  {
    this.schemeCode = schemeCode;
  }
  /**
   * @return the batch
   */
  public String getBatch()
  {
    return batch;
  }
  /**
   * @param batch the batch to set
   */
  public void setBatch(String batch)
  {
    this.batch = batch;
  }
  /**
   * @return the investmentAmount
   */
  public double getInvestmentAmount()
  {
    return investmentAmount;
  }
  /**
   * @param investmentAmount the investmentAmount to set
   */
  public void setInvestmentAmount(double investmentAmount)
  {
    this.investmentAmount = investmentAmount;
  }
  /**
   * @return the day
   */
  public String getDay()
  {
    return day;
  }
  /**
   * @param day the day to set
   */
  public void setDay(String day)
  {
    this.day = day;
  }
}

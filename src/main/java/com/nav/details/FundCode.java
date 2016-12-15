package com.nav.details;

import java.util.HashMap;
import java.util.Map;

public class FundCode
{
  Map<String, FundBasicDetails> map = new HashMap<String, FundBasicDetails>();

  /**
   * @return the map
   */
  public Map<String, FundBasicDetails> getMap()
  {
    return map;
  }

  /**
   * @param map the map to set
   */
  public void setMap(Map<String, FundBasicDetails> map)
  {
    this.map = map;
  }
  
}

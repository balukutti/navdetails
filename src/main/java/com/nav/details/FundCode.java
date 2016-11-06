/*
 *  Copyright (c) 2009-2013 Novo Dia Group Inc. All rights reserved.
 *  http://novodiagroup.com 
 */
package com.nav.details;

import java.util.HashMap;
import java.util.Map;

/**
 * @author brajendran
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
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

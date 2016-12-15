package com.nav.details;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RetreiverInitializationServlet
 */
public class RetreiverInitializationServlet extends HttpServlet {
	
  private static final long serialVersionUID = 741695668795352423L;

  public RetreiverInitializationServlet() 
  {
      super();
  }
  
  public void init() throws ServletException
  {
    Retriever retriever = new Retriever();
    retriever.scheduleSyncShipStationDataTask();
  }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

	}

}

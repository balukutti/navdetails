package com.nav.details;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ndg.ndgils.services.lookups.LicensePersistenceService;



public class Retrieve extends HttpServlet
{
  
  public class SecureRestClientTrustManager implements X509TrustManager
  {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
    {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
    {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
      return new X509Certificate[0];
    }

    public boolean isClientTrusted(X509Certificate[] arg0)
    {
      return true;
    }

    public boolean isServerTrusted(X509Certificate[] arg0)
    {
      return true;
    }
  }

  private HostnameVerifier getHostnameVerifier()
  {
    return new HostnameVerifier()
    {
      @Override
      public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession)
      {
        return true;
      }
    };
  }
  
  
  
  /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   */
  protected void processRequest(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
  {
    System.out.println("Hi Balaji");
  }

  /** Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException 
  { 
    processRequest(request,response); 
  }

  /** Handles the HTTP <code>POST</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException 
  { 
    processRequest(request,response); 
  }

  // Returns a short description of the servlet.
  public String getServletInfo() { return "Short description"; }

  // Destroys the servlet.
  public void destroy() { System.gc(); }
  
  protected Map<String,String> toMap(String string){
    Map<String,String> values = new HashMap<String, String>();
    String[] pairs = string.split("&");
    for(String pairsString : pairs){
      String[] pair = pairsString.split("=");
      if(pair.length==2){
        values.put(pair[0], pair[1]);
      }
      else{
        values.put(pair[0], "");
      }
    }
    return values;
  }


}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverlet;

import java.sql.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import superclass.Events;

/**
 *
 * @author wen
 */
@WebServlet(name = "requests_OUT", urlPatterns =
{
    "/requests_out"
})
public class requests_OUT extends HttpServlet
{

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        String id = request.getParameter("id");
        String token = request.getParameter("token");
        String start_time = request.getParameter("startdatetime");
        String start_address = request.getParameter("start_address");
        String end_address = request.getParameter("end_address");
        Add start_ll = new Add();;
        Add end_ll = new Add();
        if (start_address != null)
        {
            start_ll = convert(start_address);
        }
        if (end_address != null)
        {
            end_ll = convert(start_address);
        }
        String count = "10";
        if (request.getParameter("count") != null)
        {
            count = request.getParameter("count");
        }
        boolean isLogged = false;
        PrintWriter out = response.getWriter();
        try
        {
            Events e = new Events();
            isLogged = e.verify_token(id, token);
        } catch (Exception e)
        {
        } finally
        {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            if (isLogged)
            {
                out.println("<Requests>");
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://70.64.6.83:3306/test", "root", "test");
                    Statement stmt = con.createStatement();
                    //ResultSet rs = stmt.executeQuery("select * from request LIMIT "+count);
                    ResultSet rs = stmt.executeQuery("select * from request where start_time < '" +start_time+ "' LIMIT " + count);
                    while (rs.next())
                    {
                        int u_id = rs.getInt("id");
                        String e_stime = rs.getString("start_time");
                        String e_slat = rs.getString("start_lat");
                        String e_slon = rs.getString("start_log");
                        String u_status = rs.getString("status");
                        String u_creator = rs.getString("creator");
                        String e_elat = rs.getString("end_lat");
                        String e_elog = rs.getString("end_log");
                        String s_address = rs.getString("start_address");
                        String e_address = rs.getString("end_address");

                        out.println("<Request>");
                        out.println("<Type>Request</Type>");
                        out.println("<ID>" + u_id + "</ID>");
                        out.println("<Creator>" + u_creator + "</Creator>");
                        out.println("<StartTime>" + e_stime + "</StartTime>");
                        out.println("<StartLatitude>" + e_slat + "</StartLatitude>");
                        out.println("<StartLongitude>" + e_slon + "</StartLongitude>");
                        out.println("<Status>" + u_status + "</Status>");

                        out.println("<EndLatitude>" + e_elat + "</EndLatitude>");
                        out.println("<EndLongitude>" + e_elog + "</EndLongitude>");
                        out.println("<StartAddress>" + s_address + "</StartAddress>");
                        out.println("<EndAddress>" + e_address + "</EndAddress>");
                        out.println("</Request>");

                    }



                } catch (Exception e)
                {
                    out.print(e.toString());
                }
                out.println("</Requests>");
                //out.println(true);
            } else
            {

                out.println("<Error>");
                out.println("<id>" + id + "</id>");
                out.println("</Error>");

            }
            out.close();
        }
    }

    private Add convert(String address)
    {
        String ll = "";
        URL url = null;
        Add add = new Add();

        HttpURLConnection httpurlconnection = null;

        try
        {
            url = new URL("http://maps.google.com/maps/api/geocode/xml?address=" + address + "&sensor=true");

            httpurlconnection = (HttpURLConnection) url.openConnection();
            httpurlconnection.setDoOutput(true);
            httpurlconnection.setRequestMethod("GET");

            BufferedReader in = null;
            StringBuffer sb = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                sb.append(inputLine);
            }

            StringReader read = new StringReader(sb.toString());
            InputSource source = new InputSource(read);
            SAXBuilder sax = new SAXBuilder();

            Document doc = sax.build(source);
            Element root = doc.getRootElement();
            Element location = root.getChild("result").getChild("geometry").getChild("location");
            add.setLat(location.getChildText("lat"));
            add.setLon(location.getChildText("lng"));

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (httpurlconnection != null)
            {
                httpurlconnection.disconnect();
            }
        }
        return add;
    }

    private class Add
    {

        String lat;
        String lon;

        public Add()
        {
        }

        public void setLat(String a)
        {
            lat = a;
        }

        public void setLon(String b)
        {
            lon = b;
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>
}

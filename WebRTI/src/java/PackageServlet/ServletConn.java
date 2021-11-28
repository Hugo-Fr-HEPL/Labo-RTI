/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PackageServlet;

import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author dries
 */
public class ServletConn extends HttpServlet {

    private int cptclient = 0;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ServletConnection</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Science airport website</h2>");
            synchronized(this){
               out.println("<p>Bonjour cher " + request.getParameter("prenom") + " " + request.getParameter("nom") + " !</p>");
               cptclient++;
               out.println("<p>Vous êtes le client n° " + cptclient + ".</p>");
            }   
            if(request.getParameter("inscrip")!=null)
            {
                out.println("<p>test</p>");
                 /*try {      
                    Connection con = MySQL.MySQL_Connexion("bd_airport", "3307", "localhost", "liodrs", "liodrs");
                    if(con!=null)
                        out.println("<h2>test</h2>");
                    
                    Statement instruc = con.createStatement();
                    System.out.println("-- DB Connected --");
                    out.println("<h2>DB Connected</h2>");
                    ResultSet rs = null;

                    rs.moveToInsertRow();
                    rs.updateInt("idClient", 80);
                    rs.updateString("nomClient", request.getParameter("nom"));
                    rs.updateString("prénomClient", request.getParameter("nom"));
                    rs.updateInt("age", 0);
                    rs.updateString("sexe", "");
                    rs.updateString("mail", "");
                    rs.updateString("motdepasse", request.getParameter("pwd"));
                    rs.updateRow();

                 } catch (SQLException ex) {
                    Logger.getLogger(ServletConn.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                 
                try {
                    //Class driver = Class.forName("com.mysql.cj.jdbc.Driver");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } 
                catch (ClassNotFoundException e) {
                    System.out.println("Driver MySQL non chargé " + e.getMessage());
                }
                out.println("<p>test</p>");
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://" + "localhost" + ":" + 3307 + "/" + "bd_airport", "liodrs", "liodrs");
                    out.println("<p>Connexion à la base de donnée ok</p>");
                } 
                catch (SQLException e) {
                    System.out.println("Erreur JDBC-OBCD : " + e.getMessage() + " ** " + e.getSQLState() + "--\n\n");
                    out.println("<p>test</p>");
                }
            }
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PackageControleur;

import PackageModel.BDAccess;
import PackageModel.MySQL;
import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author dries
 */
public class ServletConn extends HttpServlet {

    private int cptclient = 0;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        if(request.getParameter("inscrip")!=null)
        {
            BDAccess bda = new BDAccess();
            Hashtable<String, String> HTId = new Hashtable<String, String>();

            bda.connection();
            bda.insertLogin(request.getParameter("nom"), request.getParameter("prenom"), request.getParameter("pwd"));

            bda.connection();
            ResultSet rs = bda.requeteLogin();

            try {
                while(rs.next())
                {
                    HTId.put(rs.getString(1), rs.getString(3));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ServletConn.class.getName()).log(Level.SEVERE, null, ex);
            }


            response.sendRedirect(request.getContextPath() + "/ServletInit?prenom="+ request.getParameter("prenom")+"&nom="+request.getParameter("nom")+"&identif="+HTId.get(request.getParameter("nom")));

        }
        else
        {
            Hashtable<String, String> HTConnection = new Hashtable<String, String>();
            Hashtable<String, String> HTId = new Hashtable<String, String>();
            BDAccess bda = new BDAccess();

            bda.connection();
            ResultSet rs = bda.requeteLogin();

            try {
                while(rs.next())
                {
                    HTConnection.put(rs.getString(1), rs.getString(2));
                    HTId.put(rs.getString(1), rs.getString(3));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ServletConn.class.getName()).log(Level.SEVERE, null, ex);
            }

            String mdp = HTConnection.get(request.getParameter("nom"));
            if(mdp!=null)
            {
                //compare mot de passe
                if(mdp.equals(request.getParameter("pwd")))
                {
                    System.out.println("Reussite");

                    response.sendRedirect(request.getContextPath() + "/ServletInit?prenom="+ request.getParameter("prenom")+"&nom="+request.getParameter("nom")+"&identif="+HTId.get(request.getParameter("nom")));
                }
                else
                {
                    System.out.println("Echec");
                    RequestDispatcher rd = request.getRequestDispatcher("index.html");
                    rd.forward(request,response);
                }
            }
            else
            {
                System.out.println("Echec");
                RequestDispatcher rd = request.getRequestDispatcher("index.html");
                rd.forward(request,response);
            }
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

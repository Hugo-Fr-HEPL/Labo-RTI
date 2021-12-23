/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package PackageControleur;

import PackageModel.BDAccess;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author dries
 */
public class ServletInit extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //session
        String IdDansCookie = request.getParameter("identif");
        
        Cookie cookie = new Cookie ("IdSession", IdDansCookie);
        response.addCookie(cookie);
        
        //Recupération date et destination disponible
        BDAccess bda = new BDAccess();
                
        bda.connection();
        ResultSet rs = bda.requetedate();
        ArrayList<String> arl1 = new ArrayList<String>();
        try {
            while(rs.next())
            {
                arl1.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServletConn.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        request.setAttribute("dates", arl1);
        
        bda.connection();
        rs = bda.requetelieu();
        ArrayList<String> arl2 = new ArrayList<String>();
        try {
            while(rs.next())
            {
                arl2.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServletConn.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        request.setAttribute("destinations", arl2);
        
        RequestDispatcher rd = request.getRequestDispatcher("/jspInit.jsp");
        rd.forward(request,response);
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

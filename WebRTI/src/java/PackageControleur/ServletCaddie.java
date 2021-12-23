/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package PackageControleur;

import PackageModel.BDAccess;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dries
 */
public class ServletCaddie extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        
        BDAccess bda = new BDAccess();
        bda.connection();
        ResultSet rs = bda.requetevols(request.getParameter("date"),request.getParameter("lieu"));
        try {
            if(rs.next())
            {
                ArrayList<ArrayList> arl = new ArrayList<ArrayList>();
                String arl1= "";

                    do
                    {
                        ArrayList<String> arltmp = new ArrayList<String>();
                        arltmp.add(rs.getString(2));
                        arltmp.add(rs.getString(3));
                        arltmp.add(rs.getString(4));
                        arltmp.add(rs.getString(5));
                        arl.add(arltmp);
                        
                        arl1 += rs.getString(1) + ";";
                    }while(rs.next());
                request.setAttribute("vols", arl);
                request.setAttribute("id",arl1);
            }
            else
            {
                request.setAttribute("vols", null);
        }
        } catch (SQLException ex) {
            Logger.getLogger(ServletConn.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher("/jspCaddie.jsp");
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

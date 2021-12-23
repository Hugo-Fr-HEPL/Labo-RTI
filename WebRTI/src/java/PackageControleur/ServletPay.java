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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dries
 */
public class ServletPay extends HttpServlet {

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
        
        StringTokenizer stk = new StringTokenizer(request.getParameter("idVol").toString(),";");
        for(int i=0;i<stk.countTokens();i++)
        {
            String numvol = stk.nextToken();
            String ligne = Integer.toString(i);
            String nbrbillet = (String) request.getParameter(ligne);
            int nbbillet = Integer.parseInt(nbrbillet);
            if(!nbrbillet.equals("0"))
            {
                //récupération du client
                String idDansCookie = null;
                Cookie[] tabCookies = request.getCookies();
                if (tabCookies != null)
                {
                    for(int j=0 ; j<tabCookies.length ; j++)
                    {
                        if ("IdSession".equals(tabCookies[j].getName()))
                            idDansCookie = tabCookies[j].getValue();
                    }
                    System.out.println(idDansCookie);
                }
                
                //places prises dans avion
                BDAccess bda = new BDAccess();
                bda.connection();
                ResultSet rs = bda.requeteplaces(numvol);
                int placeprise=0;
                try {
                    while(rs.next())
                    {
                        placeprise+=rs.getInt(1);
                        placeprise+=1;
                    }                    
                    
                } catch (SQLException ex) {
                    Logger.getLogger(ServletPay.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(placeprise);
                
                //place total avion
                bda = new BDAccess();
                bda.connection();
                rs = bda.requeteSiege(numvol);
                
                int siege=0;
                try {
                    rs.next();
                    siege = rs.getInt(1);
                } catch (SQLException ex) {
                    Logger.getLogger(ServletPay.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(siege);

                int placedispo = siege-placeprise;
                if(placedispo>=nbbillet)
                {
                    System.out.println("c'est bon");
                    //instert
                    String nbrAccomp = Integer.toString((nbbillet-1));
                    bda = new BDAccess();
                    bda.connection();
                    bda.insertBillets(idDansCookie, numvol, nbrAccomp);
                    request.setAttribute("Reussite", "Vos billets ont bien été validé!");
                }
                else
                {
                    System.out.println("c'est pas bon");
                    request.setAttribute("Reussite", "Vos billets ne sont pas validé, car il n'y a plus assez de place!");
                }
            }
            else
            {
                request.setAttribute("Reussite", "Vous n'avez pas acheté des billets pour ce vol");
            }
        }
        
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher("/jspPay.jsp");
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

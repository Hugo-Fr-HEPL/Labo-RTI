<%-- 
    Document   : JSPInit
    Created on : 18 nov. 2021, 18:29:24
    Author     : dries
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page info="Driessens Lionel - 11/2021" %>
<%@page import="java.util.*" %>
<%@page import="java.text.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Accueil</title>
    </head>
    <body>
        
        <% String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom"); %>
        
        <h3>Bienvenue sur le site de science airport Mr/Mme <%= nom %> <%=prenom%> !</h3>

        <p>(généré par <%=getServletInfo() %>)</p>
    </body>
</html>

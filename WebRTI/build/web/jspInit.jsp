<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Init</title>
        
        <style>
            body{background-image:url('C:/Users/dries/Documents/école/3eme supérieur/RTI/WebRTI/images/airportterminal.jpg')}
            body{background-repeat: no-repeat}
            body{background-size: cover}
            div{display: flex;}
            div{flex-direction: column;}
            div{text-align: center;}
        </style>
    </head>
    <body>
        <div>
            <% String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            List<String> ar1 = (List<String>) request.getAttribute("dates");
            List<String> ar2 = (List<String>) request.getAttribute("destinations");%>

            <h2>Science airport website</h2>
            <p>Bonjour cher <%= nom %> <%=prenom%></p>

            <form name="Formulaireinfos" action="ServletCaddie" method="POST">
                <p>Quand voulez-vous partir?
                <select name="date" size="1">
                <%
                for(int i=0 ; i<ar1.size() ; i++)
                {
                    String tmp = ar1.get(i);
                    %><option><%=tmp%></option><%
                }       
                %>
                </select>
                </p>
                <p>Où 
                <select name="lieu" size="1">
                <%
                for(int i=0 ; i<ar2.size() ; i++)
                {
                    String tmp = ar2.get(i);
                    %><option><%=tmp%></option><%
                }       
                %>
                </select>
                </p>

                <input type="submit" value="Acheter des billets" name="boutonAcheter" />
            </form>
        </div>
    </body>
</html>

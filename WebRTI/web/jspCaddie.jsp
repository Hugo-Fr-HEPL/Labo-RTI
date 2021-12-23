<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Caddie</title>
        
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
            <form name="Formulairebillets" action="ServletPay" method="POST">
                <table style="border-collapse: collapse">
                    <tr>
                        <td style="border-right: 1px black solid">Date de départ</td>
                        <td style="border-right: 1px black solid">Heure de départ</td>
                        <td style="border-right: 1px black solid">Lieu</td>
                        <td style="border-right: 1px black solid">Compagnie</td>
                        <td style="border-right: 1px black solid">Billets acheté</td>
                    </tr>
                <% 
                List<List> ar = (List<List>) request.getAttribute("vols");
                if(ar!=null)
                {
                    for(int i=0 ; i<ar.size() ; i++)
                    {
                        %><tr><%
                        List ltmp = ar.get(i);
                        for(int j=0 ; j<ltmp.size() ; j++)
                        {
                            String s = ltmp.get(j).toString();
                            if(j==1)
                                s+="h";
                            %><td style="border-right: 1px black solid">
                            <%= s%>
                            </td><%
                        }
                            %><td style="border-right: 1px black solid"><input type="number" name="<%=i%>" size="5" value=0 required/></td></tr><%
                    }
                }
                else
                {
                    %><h3>Pas de vol diponible pour ces critères</h3><%
                }
               %>
               </table>
               <input type="submit" value="Valider les achats" name="boutonValider" />
               <input type="text" value="<%=(String)request.getAttribute("id")%>" name="idVol" style="display: none"/>
            </form>
        </div>
    </body>
</html>

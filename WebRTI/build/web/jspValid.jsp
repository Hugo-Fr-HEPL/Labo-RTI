<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Valid</title>
        
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
            <h3><%=request.getAttribute("Ok")%></h3>
        </div>
    </body>
</html>

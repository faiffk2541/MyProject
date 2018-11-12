<%-- 
    Document   : Login
    Created on : Nov 11, 2018, 11:15:22 AM
    Author     : Ploy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        ${messege}
        <form action="Login" method="get">
            email: <input  type="text" name="email"><br>
            Password:<input  type="password" name="pass"><br>
            <input type="submit" value="submit">
            
        </form>
    </body>
</html>

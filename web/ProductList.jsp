<%-- 
    Document   : ProductList
    Created on : Nov 12, 2018, 9:40:03 PM
    Author     : Ploy
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <table>
            <thead>
            <th>Images</th>
            <th>No</th>
            <th>Product Code</th>
            <th>Product Name</th>
            <th>Product Line</th>
            <th>Price</th>
            <th>Add To Cart</th>
        </thead>
        <c:forEach items="${productList}" var="productList" varStatus="vs">
            <tr>
                <td><img src="file name/${productList.productcode}.jpg" width="120"></td>
                <td>vs.count</td>
                <td>${productList.productCode}</td>
                <td>${p.productname}</td>
                <td>${p.productline}</td>
                <td>${p.productscale}</td>
                <td>${p.msrp}</td>
<!--                <td>
                    <form action="AddItemToCart" method="post">
                        <input type="hidden" value="${p.productcode}" name="productCode"/>
                        <input type="submit" value="Add To Cart"/>
                    </form>
                    <a href="AddItemToCart?productCode=${p.productcode}">
                        <input type="button" value="Add To Cart"/>
                    </a>
                </td>-->
            </tr>
        </c:forEach>
    </table>
</body>
</html>


<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
    <h1>Dashboard Analysis</h1>
    <h2>Top 3 most used domains</h2>
    <% ArrayList<String> topDomains = (ArrayList<String>) request.getAttribute("topDomains");%>
    <% topDomains.size();%>
    <% for (int i = 0; i < topDomains.size(); i++) { %>
    <%= topDomains.get(i) %><br>
    <% } %>
    <h2>Date of highest user traffic</h2>
    <% ArrayList<String> maxDate = (ArrayList<String>) request.getAttribute("maxDate");%>
    <% maxDate.size();%>
    <% for (int i = 0; i < maxDate.size(); i++) { %>
    <%= maxDate.get(i) %><br>
    <% } %>
    <h2>Total Valid Emails</h2>
    <% int isValid = (int) request.getAttribute("countValid");%>
    <%= isValid %><br>
    <h2>Total Free Emails</h2>
    <% int isFree = (int) request.getAttribute("countFree");%>
    <%= isFree %><br>


    <h2>Data Log</h2>
    <table border="2" width="70%">
        <thead>
        <tr>
            <th>Date</th>
            <th>Email</th>
            <th>Valid Format</th>
            <th>Free Email</th>
            <th>Disposable Email</th>
            <th>Role Email</th>
        </tr>
        </thead>
        <tbody>
        <% ArrayList<String> dbConnection = (ArrayList<String>) request.getAttribute("result");%>
        <% for(String row : dbConnection) { %>
        <tr>
            <% String[] columns = row.split(" "); %>
            <td><%= columns[0] %></td>
            <td><%= columns[1] %></td>
            <td><%= columns[2] %></td>
            <td><%= columns[3] %></td>
            <td><%= columns[4] %></td>
            <td><%= columns[5] %></td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <br>
</body>
</html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Index</title>
    </head>
    <body>
        <h1 align="center">Email Validation and Verification</h1>
        <br>
        <form action="EmailValidator" method="GET">
            <label>Enter the Email ID: </label>
            <input type="text" name="email" value="" /><br><br>
            <input align="center" type="submit" value="Submit" />
        </form>
    </body>
</html>


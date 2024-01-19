/**
 Author: Siddhesh Badhan
 andrewID: sbadhan
 A HttpServlet that provides email validation functionality and dashboard.
 The servlet accepts requests for email validation and displays validation results.
 It also provides a dashboard that shows the most frequently used base and target currencies,
 as well as the most frequent dates of validation requests.
 */

package ds;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// Defining the servlet name and URL patterns using WebServlet annotation
@WebServlet(name = "EmailValidator",
        urlPatterns = {"/EmailValidator","/dashboardanalysis"})
public class EmailValidatorServlet extends HttpServlet {

    @Override
    public void init() {
    }

    /**
     This servlet handles HTTP GET requests for the Email Validation application.
     It retrieves the servlet path from the request object and performs different actions based on it.
     If the servlet path contains "EmailValidation", it retrieves email and source parameters from the request object,
     retrieves user agent from the request header, and determines whether the user's source is mobile.
     It then retrieves information about the email from the EmailValidationModel and sets the "result" attribute of the
     request object to this information. It then forwards the request to the "result.jsp" view if the email parameter is not null,
     otherwise it forwards it to the "index.jsp" view.
     If the servlet path contains "dashboard", it retrieves data from the MongoDB using the EmailValidationModel,
     retrieves frequently used domains and dates from the EmailValidationModel, and retrieves counts of valid and free emails
     from the EmailValidationModel. It then sets various attributes of the request object with this data and forwards the
     request to the "dashboardanalysis.jsp" view.
     @param request - the HTTP request object
     @param response - the HTTP response object
     @throws ServletException - if there is a servlet-related problem
     @throws IOException - if there is an I/O problem
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getServletPath();     //string to get the servlet path

        String view = "";   //empty string to store the name of the view

        if(path.contains("EmailValidator")) {

            String email = request.getParameter("email");   //retrieving email from request
            String source = request.getParameter("source"); //retreieving source from request
            String ua = request.getHeader("User-Agent");    //retrieving user agent

            boolean mobile;     //variable to store if a user's source is mobile

            if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
                // setting to the mobile version of the HTML doctype
                mobile = true;
                request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
            } else {
                // setting to the desktop version of the HTML doctype
                mobile = false;
                request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
            }

            if (email!=null) {

                String result = EmailValidatorModel.getInfo(email, source);
                request.setAttribute("result",result);

                view = "result.jsp";
            } else {
                view = "index.jsp";
            }

        }
        else if(path.contains("dashboardanalysis")){
            // Retrieving data from MongoDB using the CurrencyConverterModel class
            EmailValidatorModel.getFromMongo();
            request.setAttribute("result", EmailValidatorModel.dbLogs);

            // Creating ArrayLists to store frequently used domains and dates
            ArrayList<String> topDomains = new ArrayList<>();
            ArrayList<String> maxDate = new ArrayList<>();

            // Fetching frequently used domains
            for(int i = 0; i < 5; i++) {
                String str = EmailValidatorModel.fetchDomains();
                if(str != null) {
                    topDomains.add(str);
                }
            }
            // Setting the targetArray attribute of the request object to the topDomains ArrayList
            request.setAttribute("topDomains", topDomains);

            // Fetching frequently used dates and adding them to the maxDate ArrayList
            for(int i=0 ; i < 5; i++) {
                String str = EmailValidatorModel.fetchDate();
                if(str != null) {
                    maxDate.add(str);
                }
            }
            // Setting the freqDates attribute of the request object to the maxDate ArrayList
            request.setAttribute("maxDate", maxDate);

            // Fetch the count of valid emails from the CurrencyConverterModel and store it in the "isValid" variable
            int isValid = EmailValidatorModel.fetchIsValid();
            request.setAttribute("countValid", isValid);

            // Fetch the count of free emails from the CurrencyConverterModel and store it in the "isFree" variable
            int isFree = EmailValidatorModel.fetchIsFree();
            request.setAttribute("countFree", isFree);


            // Setting the value of nv to "dashboardanalysis.jsp"
            view = "dashboardanalysis.jsp";
        }

        // Forwarding the request and response objects to the dashboardanalysis.jsp file using a RequestDispatcher
        RequestDispatcher requestDispatcherView = request.getRequestDispatcher(view);
        requestDispatcherView.forward(request, response);
    }
}
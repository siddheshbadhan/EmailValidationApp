/**
 * Author: Siddhesh Badhan
 * andrewID: sbadhan
 * This class represents an email verification and validation model. It provides functionality of validating and verifying
 * any email address and interacts with a MongoDB database to store the logs and receive analytics data.
 */

package ds;

import com.mongodb.client.*;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmailValidatorModel {
    static PriorityQueue<String> emailQ = null; //queue to store the input email
    static PriorityQueue<String> datesQ = null;  //queue to store the dates
    static ArrayList<String> dbLogs = null; //array list to store logs
    static String isMobile;     //variable to check if device is mobile
    static int isValid = 0; //variable to store the count of Valid Emails
    static int isFree = 0;  //variable to store the count of Free Emails

    /**
     Gets information about an email address using the Abstract API service and stores the data in a MongoDB database.
     @param email The email address to validate
     @param source The source of the API call (e.g. "mobile" for calls made from a mobile device)
     @return A JSON string containing information about the email address, including whether it is a valid, free, disposable, or role email address
     */
    public static String getInfo(String email, String source) {

        isMobile = source;

        String isValid = null;  //variable to check if email is valid email
        String isFree = null;   //variable to check if email is free email
        String isDisposable = null; //variable to check if email is disposable email
        String isRole = null;   //variable to check if email is role email

        String api_address = "https://emailvalidation.abstractapi.com/v1/?api_key=142b219a8606471e9f033e1862c376bb&email=" + email;
        String result = fetch(api_address);
        JSONObject json = (JSONObject) JSONValue.parse(result);

        // Extract the "is_valid_format" object from the JSON object and get the "text" value as a String
        JSONObject isValidFormat = (JSONObject) json.get("is_valid_format");
        isValid = (String) isValidFormat.get("text");

        // Extract the "is_free_email" object from the JSON object and get the "text" value as a String
        JSONObject isFreeEmail = (JSONObject) json.get("is_free_email");
        isFree = (String) isFreeEmail.get("text");

        // Extract the "is_disposable_email" object from the JSON object and get the "text" value as a String
        JSONObject isDisposableEmail = (JSONObject) json.get("is_disposable_email");
        isDisposable = (String) isDisposableEmail.get("text");

        // Extract the "is_role_email" object from the JSON object and get the "text" value as a String
        JSONObject isRoleEmail = (JSONObject) json.get("is_role_email");
        isRole = (String) isRoleEmail.get("text");


        // Get the current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date(System.currentTimeMillis()));
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Get MongoDB connection and store the conversion data
        getFromMongo();
        pushToMongo(email, isValid, isFree, isDisposable, isRole, date, time);

        // Return the converted amount as a JSON string
        return sendToAndroid(isValid, isFree, isDisposable, isRole);
    }

    /**
     * Fetches the content from the specified URL and returns it as a String.
     *
     * @param urlString the URL to fetch the content from
     * @return the content fetched from the URL as a String, or null if an exception occurred
     * @throws IOException if an I/O error occurs while opening the connection or reading the input stream
     */
    static private String fetch(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line); // Append each line to the StringBuilder
            }

            in.close();
            return response.toString();
        } catch (IOException e) {
            System.out.println("An exception occurred: " + e.getMessage());
            return null;
        }
    }

    /**

     This method pushes email analytics data to MongoDB if the API call is made from a mobile device.
     It creates a MongoDB client and accesses the "Project4" database. It inserts a new document in the "Test4Logs" collection with the following fields:
     time: The time at which the API was called
     email: The email address for which analytics are being fetched
     isValid: A boolean indicating whether the email address is valid or not
     isFree: A boolean indicating whether the email address is free or not
     isDisposable: A boolean indicating whether the email address is disposable or not
     isRole: A boolean indicating whether the email address is a role account or not
     date: The date at which the API was called
     @param email The email address for which analytics are being fetched
     @param isValid A boolean indicating whether the email address is valid or not
     @param isFree A boolean indicating whether the email address is free or not
     @param isDisposable A boolean indicating whether the email address is disposable or not
     @param isRole A boolean indicating whether the email address is a role account or not
     @param date The date at which the API was called
     @param time The time at which the API was called
     */
    static private void pushToMongo(String email, String isValid, String isFree, String isDisposable, String isRole, String date, String time) {
        String mongoURL = "mongodb+srv://siddhesh:temporary@cluster0.uu4elge.mongodb.net/?retryWrites=true&w=majority";

        if (isMobile == null) {
            //do nothing, store only logs where API calls are made from mobile
        } else if (isMobile.equals("mobile")) {
            try (MongoClient client = MongoClients.create(mongoURL)) {
                MongoDatabase mongo = client.getDatabase("Project4");
                MongoCollection mongoCollection = mongo.getCollection("Test4Logs");
                Document d = new Document("time", time);
                d.append("email", email);
                d.append("isValid", isValid);
                d.append("isFree", isFree);
                d.append("isDisposable", isDisposable);
                d.append("isRole", isRole);
                d.append("date", date);
                mongoCollection.insertOne(d);
            }
        }
    }

    /**
     Fetches data from MongoDB and performs analytics on it.
     The method connects to a MongoDB database using the provided connection URL,
     retrieves data from the "Test4Logs" collection in the "Project4" database,
     and performs analytics to extract information such as email domains, dates, and counters for valid and free emails.
     */
    static public void getFromMongo() {
        // MongoDB connection URL
        String mongoURL = "mongodb+srv://siddhesh:temporary@cluster0.uu4elge.mongodb.net/?retryWrites=true&w=majority";

        // Create a MongoDB client
        try (MongoClient mongoClient = MongoClients.create(mongoURL)) {
            // Access the "Project4" database
            MongoDatabase db = mongoClient.getDatabase("Project4");
            // Access the "CurrencyConversionLog" collection
            MongoCollection mongoCollection = db.getCollection("Test4Logs");
            // Find all documents in the collection
            FindIterable<Document> d = mongoCollection.find();
            dbLogs = new ArrayList<>();
            // Create a cursor to iterate through the documents
            MongoCursor<Document> mongoCursor = d.iterator();
            // Perform analytics on the fetched data
            dashboardAnalyser(mongoCursor);
        }
    }

    /**

     Constructs a JSON object with the given input parameters and returns its string representation.
     The JSON object contains information about the validity, freeness, disposability, and role of an email.
     @param isValid a String representing the number of valid emails
     @param isFree a String representing the number of free emails
     @param isDisposable a String representing the number of disposable emails
     @param isRole a String representing the number of emails with role account
     @return a String representing the JSON object containing the input parameters
     */
    static private String sendToAndroid(String isValid, String isFree, String isDisposable, String isRole) {
        JSONObject json = new JSONObject();
        json.put("isValid", isValid);
        json.put("isFree", isFree);
        json.put("isDisposable", isDisposable);
        json.put("isRole", isRole);
        return json.toString();
    }

    /**
     Analyzes the dashboard data from a given MongoDB cursor and updates relevant data structures.
     @param mongoCursor the MongoDB cursor to analyze
     */
    static private void dashboardAnalyser(MongoCursor<Document> mongoCursor) {
        Map<String, Integer> emailMap = new HashMap<>();
        Map<String, Integer> datesMap = new HashMap<>();
        emailQ = new PriorityQueue<>();
        datesQ = new PriorityQueue<>();


        while (mongoCursor.hasNext()) {
            // Get the email from the current document
            Document d = mongoCursor.next();
            String temp = (String) d.get("email");
            // Split the email to get the domain
            String[] parts = temp.split("@");
            String domain = parts[1];

            // Update emailMap to count the number of occurrences of each domain
            emailMap.putIfAbsent(domain, 0);
            emailMap.put(domain, emailMap.get(domain) + 1);

            // Update datesMap to count the number of occurrences of each date
            datesMap.putIfAbsent((String) d.get("date"), 0);
            datesMap.put((String) d.get("date"), datesMap.get(d.get("date")) + 1);

            // Check if the email is valid and update the counter if it is
            if (Boolean.parseBoolean((String) d.get("isValid"))) {
                isValid = isValid + 1;
            }
            // Check if the email is free and update the counter if it is
            if (Boolean.parseBoolean((String) d.get("isFree"))) {
                isFree = isFree + 1;
            }

            // Add a log entry to dbLogs for the current document
            dbLogs.add(d.get("date") + " " + d.get("email") + " " + d.get("isValid") + " " + d.get("isFree") + " " + d.get("isDisposable") + " " + d.get("isRole"));
        }

        // Update priority queues based on the email and date maps
        updateQueueFromMap(emailMap, emailQ);
        updateQueueFromMap(datesMap, datesQ);

    }

    /**

     Updates the given queue with the keys from the map.
     If the size of the queue exceeds 3, the oldest element is removed.
     @param map the map containing the keys to add to the queue
     @param queue the queue to update
     */
    private static void updateQueueFromMap(Map<String, ?> map, Queue<String> queue) {
        // Iterate over the keys in the map
        for (String key : map.keySet()) {
            // Add the key to the queue
            queue.add(key);
            // If the size of the queue exceeds 3, remove the oldest element
            if (queue.size() > 3) {
                queue.poll();
            }
        }
    }

    /**
     Returns the next domain in the email queue and removes it from the queue.
     @return the next domain in the email queue
     */
    public static String fetchDomains() {
        return emailQ.poll();
    }

    /**
     Returns the next date in the dates queue and removes it from the queue.
     @return the next date in the dates queue
     */
    public static String fetchDate() {
        return datesQ.poll();
    }

    /**
     Returns the total number of valid emails.
     @return the total number of valid emails
     */
    public static int fetchIsValid() {
        return isValid;
    }

    /**
     Returns the total number of free emails.
     @return the total number of free emails
     */
    public static int fetchIsFree() {
        return isFree;
    }

}
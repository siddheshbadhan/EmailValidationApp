/*
 * Author: Siddhesh Badhan
 * andrewID: sbadhan
 *
 * The MainActivity class defines the functionality for a currency converter Android application.
 * It allows users to input an email address, and sends a request to an API to validate and retrieve information
 * about the email address. The class initializes views and sets up event listeners for the submit and reset buttons.
 * The API response is displayed to the user in the output view.
   @author [Siddhesh Badhan]
   @version 1.0
   @since 2023-04-10
 *
 */

package edu.cmu.EmailValidator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmu.currencyconverter.R;

public class MainActivity extends AppCompatActivity {
    EditText email; // Variable for taking input Email from user
    TextView output; // variable for displaying output to the user
    Button submitButton, resetButton; // Submit and Reset Buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calling the super class's onCreate method
        setContentView(R.layout.activity_main); // Setting the content view to "activity_main" layout

        email = findViewById(R.id.email); // Initializing "email" with the input email taken from user
        output = findViewById(R.id.output); // Initializing "output" with the output view to be displayed to the user
        submitButton = findViewById(R.id.submitButton); // Initializing submit button
        resetButton = findViewById(R.id.resetButton); // Initializing reset button

        setupListeners();
    }

    void setupListeners() {

        // Set up event listener for submit button
        submitButton.setOnClickListener(v -> {

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                String response = "";

                @Override
                public void run() {
                    try {
                        // Construct URL for API call
                        //https://siddheshbadhan-reimagined-lamp-x4w7r7pggq297jx-8080.preview.app.github.dev/
                        URL url = new URL("https://siddheshbadhan-reimagined-space-r66x97g6wrqfwpgx-8080.preview.app.github.dev/CurrencyConverter?email=" + email.getText().toString() + "&source=mobile");
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        // Read response from API call
                        BufferedInputStream buffered = new BufferedInputStream(connection.getInputStream());
                        byte[] bytes = new byte[1024];
                        int numberOfBytes;
                        while ((numberOfBytes = buffered.read(bytes)) != -1) {
                            response += new String(bytes, 0, numberOfBytes);
                        }
                    } catch (Exception e) {
                        // Invalidate view if there is an error
                        v.invalidate();
                    }

                    // Update UI with converted amount
                    runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            String isValidString = json.getString("isValid"); //Variable for storing if Email is Valid
                            String isFreeString = json.getString("isFree"); //Variable for storing if Email is Free
                            String isDisposableString = json.getString("isDisposable"); //Variable for storing if Email is Disposable
                            String isRoleString = json.getString("isRole"); //Variable for storing if Email is Role based
                            //setting up output to be displayed to user
                            output.setText("Valid Format: " + isValidString + "\nFree Email: " + isFreeString + "\nDisposable Email: " + isDisposableString + "\nRole Email: " + isRoleString);
                        } catch (Exception e) {
                            // Invalidate view if there is an error
                            v.invalidate();
                        }
                    });
                }
            });
        });
        //reset button which clears all the parameters and gives an opportunity to start over
        resetButton.setOnClickListener(v -> {
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> runOnUiThread(() -> {
                try {
                    output.setText(null); // clear the output by setting it to null
                    email.setText(null); // clear the input by setting it to null
                    email.setHint(getString(R.string.emailHint)); // set the Hint to prompt user for an email
                } catch (Exception e) {
                    v.invalidate();
                }
            }));
        });
    }
}
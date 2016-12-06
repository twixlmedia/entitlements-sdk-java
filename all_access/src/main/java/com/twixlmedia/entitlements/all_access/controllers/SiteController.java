/*
 * Twixl Publisher
 *
 * Copyright (c) Twixl media. All rights reserved.
 */

package com.twixlmedia.entitlements.all_access.controllers;

import com.twixlmedia.entitlements.all_access.models.EntitlementsModel;
import com.twixlmedia.entitlements.all_access.responses.EntitlementsResponse;
import com.twixlmedia.entitlements.all_access.models.SigninModel;
import com.twixlmedia.entitlements.all_access.responses.ErrorResponse;
import com.twixlmedia.entitlements.all_access.responses.SigninResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class SiteController {

    // The entitlement token used to identify a user which has full access
    //
    // For security reasons, change this to a custom value
    private String defaultToken = "d6b3959ecdce4cc29830bcfc0473c938";

    // The default page which is shown when no action is specified
    @GetMapping(value = "/")
    public String actionIndex(Model model) {
        model.addAttribute("title", "All Access Twixl Entitlements Server");
        return "site/index";
    }

    // Shows the signin form used to ask for the username and password
    @GetMapping(value = "/signin_form")
    public String actioSigninForm(Model model) {
        model.addAttribute("title", "Login");
        return "site/signin_form";
    }

    // Check the username and password from the signin form and return an entitlement token uniquely identifying this
    // user on the entitlement server.
    //
    // As we are only interested in finding out if the user has access or not, we return the same entitlement token for
    // all users.
    //
    // For added security, you can return a unique token for each user which can then be verified in the
    // entitlements method.
    //
    // If the login fails, we return an error message.
    //
    // The different parameters are sent as a HTTP POST request.
    @PostMapping(value = "/signin", produces = "application/json")
    public ResponseEntity<Object> actionSignin(@ModelAttribute SigninModel signinModel) {
        try {

            String token = checkLogin(signinModel.getUsername(), signinModel.getPassword());

            SigninResponse response = new SigninResponse(token);
            return okResponse(response);

        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    // The signin succeeded, so we are just closing the entitlements popup by calling a specific url
    @GetMapping(value = "/signin_succeeded")
    public String actionSigninSucceeded(@RequestParam("token") String token, Model model) {
        return "redirect:tp-close://self";
    }

    // The signin didn't work, we retrieve the error from the URL and render the error screen.
    @GetMapping(value = "/signin_error")
    public String actionSigninError(@RequestParam("error") String error, Model model) {
        model.addAttribute("title", "An Error Occurred");
        model.addAttribute("error", error);
        return "site/signin_error";
    }

    // The entitlements call checks the token, and decides based on the token if the user has access to content or not.
    //
    // If the token is empty, we return an empty list of allowed product identifiers and use the entitlement mode
    // "hide_unentitled". This causes the app to not show any content.
    //
    // If the token is correct, we return "*" as the entitled_products which tells the app to show all content.
    //
    // If the token is invalid, we return an error message.
    //
    // The different parameters are sent as a HTTP POST request.
    @PostMapping(value = "/entitlements", produces = "application/json")
    public ResponseEntity<Object> actionEntitlements(@ModelAttribute EntitlementsModel entitlementsModel) {
        try {

            ArrayList<String> entitledProducts = new ArrayList<>();

            if (entitlementsModel.getToken() == null || entitlementsModel.getToken().isEmpty()) {
            } else if (entitlementsModel.getToken().equalsIgnoreCase(defaultToken)) {
                entitledProducts.add("*");
            } else {
                throw new Exception("Invalid credentials");
            }

            EntitlementsResponse response = new EntitlementsResponse();
            response.setEntitledProducts(entitledProducts);
            response.setMode("hide_unentitled");
            response.setToken(entitlementsModel.getToken());
            return okResponse(response);

        } catch (Exception e) {
            return errorResponse(e);
        }

    }

    // This is a helper method to check if the login is correct or not.
    //
    // If the login is correct, we return an entitlement token.
    //
    // If the loign is incorrect, we throw an Exception.
    //
    // This is the place where you can customize the way a username and password are verified. You can for example
    // perform a database call to verify the credentials.
    private String checkLogin(String username, String password) throws Exception {
        if (!username.equalsIgnoreCase("test") && !password.equalsIgnoreCase("test")) {
            throw new Exception("Invalid username or password");
        }
        return defaultToken;
    }

    // Helper method to return a HTTP 200 response
    private ResponseEntity<Object> okResponse(Object response) {
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Helper method to return a HTTP 500 response
    private ResponseEntity<Object> errorResponse(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

/*
 * Twixl Publisher
 *
 * Copyright (c) Twixl media. All rights reserved.
 */

package com.twixlmedia.entitlements.regions.controllers;

import com.twixlmedia.entitlements.regions.models.EntitlementsModel;
import com.twixlmedia.entitlements.regions.responses.EntitlementsResponse;
import com.twixlmedia.entitlements.regions.models.SigninModel;
import com.twixlmedia.entitlements.regions.responses.ErrorResponse;
import com.twixlmedia.entitlements.regions.responses.SigninResponse;
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

    // The default page which is shown when no action is specified
    @GetMapping(value = "/")
    public String actionIndex(Model model) {
        model.addAttribute("title", "Regions Twixl Entitlements Server");
        return "site/index";
    }

    // Shows the signin form used to ask for the region
    @GetMapping(value = "/signin_form")
    public String actionSigninForm(Model model) {
        model.addAttribute("title", "Select your region");
        return "site/signin_form";
    }

    // Check the region from the signin form and return it as the entitlement token. This will allow us to return the
    // correct list of entitlements later on.
    //
    // If the region is invalid, we return an error message.
    //
    // The different parameters are sent as a HTTP POST request.
    @PostMapping(value = "/signin", produces = "application/json")
    public ResponseEntity<Object> actionSignin(@ModelAttribute SigninModel signinModel) {
        try {

            String token = checkRegion(signinModel.getRegion());

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

    // The entitlements call checks the token to find out what region was selected. Based on the region, it will return
    // a different list of product identifiers which combined with the "hide_unentitled" mode makes the app show or hide
    // different issues.
    //
    // If the token is invalid, we return an error message.
    //
    // The different parameters are sent as a HTTP POST request.
    @PostMapping(value = "/entitlements", produces = "application/json")
    public ResponseEntity<Object> actionEntitlements(@ModelAttribute EntitlementsModel entitlementsModel) {
        try {

            ArrayList<String> entitledProducts = new ArrayList<>();

            if (entitlementsModel.getToken().equalsIgnoreCase("region1")) {
                entitledProducts.add("com.twixlmedia.demo.region1.issue1");
                entitledProducts.add("com.twixlmedia.demo.region1.issue2");
            } else if (entitlementsModel.getToken().equalsIgnoreCase("region2")) {
                entitledProducts.add("com.twixlmedia.demo.region2.issue1");
                entitledProducts.add("com.twixlmedia.demo.region2.issue2");
            } else {
                throw new Exception("Invalid region");
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
    private String checkRegion(String region) throws Exception {
        if (!region.equalsIgnoreCase("region1") && !region.equalsIgnoreCase("region1")) {
            throw new Exception("Invalid region");
        }
        return region;
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

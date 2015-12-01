/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apifocal.wsman.cli;

/**
 *
 * @author chris
 * 
 * output of an executed wsman command
 */
public class Response {
    public int statusCode;
    public String std_out;
    public String std_err;

    public Response(int statusCode, String std_out, String std_err) {
        this.statusCode = statusCode;
        this.std_out = std_out;
        this.std_err = std_err;
    }
}

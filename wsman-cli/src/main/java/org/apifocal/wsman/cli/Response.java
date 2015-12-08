package org.apifocal.wsman.cli;

/**
 * Output of an executed wsman command.
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

package org.apifocal.wsman.cli;

/**
 * Output of an executed wsman command.
 */
public class CommandOutput {
    public int statusCode = 0;
    public String std_out = new String();
    public String std_err = new String();
}

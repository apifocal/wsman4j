/*
 * Copyright 2015 apifocal.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apifocal.wsman.cli;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

/**
 * wsman client application
 */
public class WsmanCli {

    @Option(name = "-host", usage = "Sets a hostname")
    public String host;

    @Option(name = "-port", usage = "Sets a port number")
    public int port = 0;

    @Option(name = "-user", usage = "Sets a username")
    public String user;

    @Option(name = "-pass", usage = "Sets a password")
    public String pass;

    @Option(name = "-transport", usage = "Sets a transport")
    public EnumTransport transport = EnumTransport.plaintext;

    @Option(name = "-cmd", usage = "Sets a command")
    public String cmd;
    
    @Option(name = "-cmdargs", handler = StringArrayOptionHandler.class, required = true)
    private List<String> cmdArgs;
    
    @Option(name = "-ps", usage = "Sets a powershell script")
    public String ps;

    public void run() throws MalformedURLException {
        final String protocol = transport == EnumTransport.ssl ? "https" : "http";
        if (port == 0) {
            port = transport == EnumTransport.ssl ? 5986 : 5985;
        }

        URL endpoint = new URL(protocol, host, port, "/wsman");

        System.out.println("Connecting to " + endpoint.toString());

        //execute wsman shell commands
        Session s = new Session(endpoint, transport);
        
        if (!cmd.isEmpty())
            s.runCmd(cmd, cmdArgs.toArray(new String[cmdArgs.size()]));
        
        if (!ps.isEmpty())
            s.runPs(ps);
    }

    //entry point
    public static void main(String[] args) {
        WsmanCli w = new WsmanCli();
        CmdLineParser parser = new CmdLineParser(w);
        try {
            parser.parseArgument(args);
            w.run();
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WsmanCli.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

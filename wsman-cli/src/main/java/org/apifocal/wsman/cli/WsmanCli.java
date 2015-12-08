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
package org.apifocal.wsman.cli;

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
    public Transport transport = Transport.plaintext;

    @Option(name = "-cmd", usage = "Sets a command")
    public String cmd;

    @Option(name = "-cmdArgs", handler = StringArrayOptionHandler.class, required = true)
    public List<String> cmdArgs = null;

    @Option(name = "-ps", usage = "Sets a powershell script")
    public String ps = null;

    public Session createSession() throws MalformedURLException {
        final String protocol = transport == Transport.ssl ? "https" : "http";
        if (port == 0) {
            port = transport == Transport.ssl ? 5986 : 9985;
        }

        URL endpoint = new URL(protocol, host, port, "/wsman");

        System.out.println("Connecting to " + endpoint.toString());

        Session s = new Session(endpoint, transport);

        return s;
    }

    public void run() throws MalformedURLException {
        Session s = createSession();

        //execute wsman shell commands
        if (!cmd.isEmpty()) {
            s.runCmd(cmd, cmdArgs.toArray(new String[cmdArgs.size()]));
        }

        if (!ps.isEmpty()) {
            s.runPs(ps);
        }
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

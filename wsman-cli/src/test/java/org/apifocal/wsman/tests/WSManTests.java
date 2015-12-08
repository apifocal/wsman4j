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
package org.apifocal.wsman.tests;

import org.apifocal.wsman.cli.WsmanCli;
import org.apifocal.wsman.cli.Response;
import org.apifocal.wsman.cli.Session;
import org.apifocal.wsman.cli.Transport;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class WSManTests {

    public WSManTests() {
    }

    @BeforeClass
    public static void setUpClass() {
        //test is only runnable in a windows host
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        org.junit.Assume.assumeTrue(isWindows); //failure causes the test to be ignored

        //configure WinRM
        try {
            Runtime.getRuntime().exec("cmd /c start setupWinRM.bat");
        } catch (IOException ex) {
            Logger.getLogger(WSManTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    //
    //a basic test: execute a shell command and check the exit code
    //
    @Test
    public void testWinRM() throws Exception {
        WsmanCli w = new WsmanCli();
        w.host = "localhost";
        w.port = 5985;
        w.user = "user";
        w.pass = "password";
        w.transport = Transport.plaintext;
        //w.cmd
        //w.cmdArgs

        Session s = w.createSession();
        Response resp = s.runCmd("ipconfig", "/all");
        assertTrue(resp.statusCode == 0);
    }
}

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

import java.io.FileNotFoundException;
import org.apifocal.wsman.cli.WsmanCli;
import org.apifocal.wsman.cli.Session;
import org.apifocal.wsman.cli.Transport;
import org.dmtf.schemas.wbem.wsman._1.wsman.CommandResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class WSManTests {
    
    private WsmanCli cli = new WsmanCli();

    public WSManTests() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        //test is only runnable in a windows host
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        org.junit.Assume.assumeTrue(isWindows); //failure causes the test to be ignored

        //make sure to configure WinRM prior to running tests
        //Runtime.getRuntime().exec("cmd /c start setupWinRM.bat");        
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        //load user configuration
        Properties prop = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("configTests.properties");
        if (is != null)
            prop.load(is);
        else
            throw new FileNotFoundException("tests config file missing");
        
        cli.host = prop.getProperty("host");
        cli.port = Integer.parseInt(prop.getProperty("port"));
        cli.user = prop.getProperty("user");
        cli.pass = prop.getProperty("pass");
        cli.transport = Transport.plaintext;
        //cli.cmd = prop.getProperty("cmd");
        //cli.cmdArgs = Arrays.asList(prop.getProperty("cmdArgs").split(" "));        
    }

    @After
    public void tearDown() {
    }

    //
    //a basic test: execute a shell command and check the exit code
    //
    @Test
    public void testWinRM() throws Exception {
        Session s = cli.createSession();
        CommandResponse resp = s.runCmd("ipconfig", "/all");
        assertTrue(true);
    }
}

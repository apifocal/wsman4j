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

import com.microsoft.schemas.wbem.wsman._1.windows.shell.Shell;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import javax.xml.bind.JAXB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xmlsoap.schemas.ws._2004._09.transfer.CreateResponseType;

/**
 *
 */
public class WinRMIT {

    private WsmanCli cli;

    public WinRMIT() {
    }

//    @BeforeClass
//    public static void setUpClass() {
//        //make sure to configure WinRM prior to running tests
//    }

//    @AfterClass
//    public static void tearDownClass() {
//    }

    @Before
    public void setUp() throws Exception {
        // TODO configure the fixture externally
//        WinRMFixture fixture = new PropfileWinRMFixture();
        WinRMFixture fixture = new SyspropsWinRMFixture();
        cli = fixture.createClient();

//        if ("localhost".equals(cli.host)) {
//            //test is only runnable in a windows host
//            boolean isWindows = System.getProperty("os.name").startsWith("Windows");
//            assumeTrue("This tests needs a windows host", isWindows); //failure causes the test to be ignored
//        }
    }

    @After
    public void tearDown() {
    }

    /*
     * basic test that checks that a command is properly executed by wsman
     */
    @Test
    public void testWsman() throws Exception {
        Session s = cli.createSession();

        CommandOutput out = s.runCmd("ipconfig", "/all");

        //check that wsman executed the specified command with success
        assertTrue(out.statusCode == 0);
    }

    /*
     * make sure that CXF generated code loads the same ws response as pywinrm
     * compares Shell objects inside ws responses
     */
    @Test
    public void testCXF() throws Exception {
        //
        //load sample response captured with pywinrm
        //
        String xml = new Scanner(new File("../wsman-api/sample/wsman.soap.session/resp1.xml")).useDelimiter("\\Z").next();
        CreateResponseType sampleResponse = JAXB.unmarshal(new StringReader(xml), CreateResponseType.class);

        //
        //compare with same request executed with wsman
        //                
        URL url = new URL("http", cli.host, cli.port, "/wsman");
        ITransport transport = new BasicAuth(cli.user, cli.pass);

        //create protocol
        Protocol protocol = new Protocol(url, transport);

        //open shell
        //String shellId = protocol.openShell();
        final boolean noprofile = false; //@TODO@ make param
        final int codepage = 437; //@TODO@ make param

        String messageId = UUID.randomUUID().toString();
        HashMap<String, String> options = new HashMap<>();
        options.put("WINRS_NOPROFILE", noprofile ? "TRUE" : "FALSE");
        options.put("WINRS_CODEPAGE", String.valueOf(codepage));

        protocol.prepareRequest(Protocol.URI_ACTION_CREATE, null, messageId, options);

        //add SOAP body
        Shell shell = new Shell();
        shell.getOutputStreams().add("stdout stderr");
        shell.getInputStreams().add("stdin");

        //ws call
        CreateResponseType response = protocol.wsmanService.create(shell);

        Shell sh = (Shell) response.getAny();
        String shellId = sh.getShellId();

        assertEquals(sh, (Shell) sampleResponse.getAny()); //Shells inside responses should match

        //run command, get output, cleanup and close shell
        //String commandId = protocol.runCommand(shellId, "ipconfig", "/all".split(" "));
        //CommandOutput out = protocol.getCommandOutput(shellId, commandId);
        //protocol.cleanupCommand(shellId, commandId);
        protocol.closeShell(shellId);
    }
}

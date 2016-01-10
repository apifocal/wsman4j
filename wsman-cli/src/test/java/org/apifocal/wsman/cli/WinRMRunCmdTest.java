/*
 * Copyright 2016 apifocal.
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

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.XPathBody.xpath;

/**
 * Unit-test WinRM shell commands against a mock server.
 */
public class WinRMRunCmdTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerAccessor;

    private WsmanCli cli;

    @Before
    public void mockWinrmResponses() throws Exception {
        mapSoapAction("http://schemas.xmlsoap.org/ws/2004/09/transfer/Create",
                "resp1-create.xml");
        mapSoapAction("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Command",
                "resp2-command.xml");
        mapSoapAction("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Receive",
                "resp3-receive.xml");
        mapSoapAction("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Signal",
                "resp4-signal.xml");
        mapSoapAction("http://schemas.xmlsoap.org/ws/2004/09/transfer/Delete",
                "resp5-delete.xml");
    }

    private void mapSoapAction(String soapAction, String responsePath) throws IOException {
        mockServerAccessor
                .when(request()
                        .withBody(xpath("/Envelope/Header[Action = '" + soapAction + "']"))
                ).respond(response()
                        .withStatusCode(200)
                        .withBody(resource("fixtures/runCmd/" + responsePath))
                );
    }

    @Before
    public void createWinrmClient() throws Exception {
        WinRMFixture fixture = new MockserverWinRMFixture(mockServerRule);
        cli = fixture.createClient();
    }

    private String resource(String path) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        return IOUtils.toString(is);
    }

    // TODO: call each method
    @Test
    public void testRunCmd() throws Exception {
        Session s = cli.createSession();

        CommandOutput out = s.runCmd("ipconfig", "/all");

        //check that wsman executed the specified command with success
        assertTrue(out.statusCode == 0);
    }

}

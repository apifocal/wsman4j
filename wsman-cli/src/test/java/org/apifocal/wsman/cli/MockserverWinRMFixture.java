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

import org.mockserver.junit.MockServerRule;
import org.mockserver.mockserver.MockServer;

/**
 * Provides WinRM test fixture for an {@link MockServer}.
 */
public class MockserverWinRMFixture implements WinRMFixture {

    private final Integer port;

    MockserverWinRMFixture(MockServer server) {
        this.port = server.getPort();
    }

    MockserverWinRMFixture(MockServerRule rule) {
        this.port = rule.getHttpPort();
    }

    @Override
    public WsmanCli createClient() {
        WsmanCli cli = new WsmanCli();
        cli.host = "localhost";
        cli.port = port;
        cli.user = "mockuser";
        cli.pass = "mockpassword";
        cli.transport = Transport.PLAINTEXT;
        //cli.cmd = prop.getProperty(WINRM_CMD);
        //cli.cmdArgs = Arrays.asList(prop.getProperty(WINRM_CMDARGS).split(" "));

        return cli;
    }

}

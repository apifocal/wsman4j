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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides WinRM test fixture from a properties file.
 */
public class PropfileWinRMFixture implements WinRMFixture {

    @Override
    public WsmanCli createClient() throws IOException {
        //load user configuration
        Properties prop = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("configTests.properties");
        if (is != null) {
            prop.load(is);
        } else {
            throw new FileNotFoundException("tests config file missing");
        }

        WsmanCli cli = new WsmanCli();
        cli.host = prop.getProperty(WINRM_HOST);
        cli.port = Integer.parseInt(prop.getProperty(WINRM_PORT));
        cli.user = prop.getProperty(WINRM_USER);
        cli.pass = prop.getProperty(WINRM_PASS);
        cli.transport = Transport.plaintext;
        //cli.cmd = prop.getProperty(WINRM_CMD);
        //cli.cmdArgs = Arrays.asList(prop.getProperty(WINRM_CMDARGS).split(" "));

        return cli;
    }

}

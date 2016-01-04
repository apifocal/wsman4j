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

/**
 * Provides WinRM test fixture from a set of Java system properties.
 *
 * Usage sample:
 *
 * In pom.xml:
 *
 * <code>
 * &lt;properties&gt;
 *     &lt;winrm.host&gt;localhost&lt;/winrm.host&gt;   &lt;!-- some defaults -- &gt;
 *     &lt;winrm.host&gt;localhost&lt;/winrm.host&gt;
 *     .......
 * &lt;/properties&gt;
 * ......
 * &lt;plugin&gt;
 *      &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *      &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *      &lt;configuration&gt;
 *         &lt;systemProperties&gt;
 *           &lt;property&gt;
 *              &lt;name&gt;winrm.host&lt;/name&gt;
 *              &lt;value&gt;${winrm.host}&lt;/value&gt;
 *           &lt;/property&gt;
 *           &lt;property&gt;
 *              &lt;name&gt;winrm.port&lt;/name&gt;
 *              &lt;value&gt;${winrm.port}&lt;/value&gt;
 *           &lt;/property&gt;
 *           ......
 *         &lt;/systemProperties&gt;
 *      &lt;/configuration&gt;
 * &lt;/plugin&gt;
 * </code>
 *
 * Next run it with:
 *
 * <code>mvn -Dwinrm.host=someHost test </code>
 */
public class SyspropsWinRMFixture implements WinRMFixture {

    @Override
    public WsmanCli createClient() {
        WsmanCli cli = new WsmanCli();
        cli.host = System.getProperty(WINRM_HOST);
        cli.port = Integer.parseInt(System.getProperty(WINRM_PORT));
        cli.user = System.getProperty(WINRM_USER);
        cli.pass = System.getProperty(WINRM_PASS);
        cli.transport = Transport.plaintext;
        //cli.cmd = prop.getProperty("cmd");
        //cli.cmdArgs = Arrays.asList(prop.getProperty("cmdArgs").split(" "));

        return cli;
    }
}

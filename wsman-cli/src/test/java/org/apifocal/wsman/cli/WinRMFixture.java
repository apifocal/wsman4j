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
 * The WinRM system for tests.
 */
public interface WinRMFixture {

    // convenience common property names for various config files
    public static final String WINRM_HOST = "winrm.host";
    public static final String WINRM_PORT = "winrm.port";
    public static final String WINRM_USER = "winrm.user";
    public static final String WINRM_PASS = "winrm.pass";
    public static final String WINRM_CMD = "winrm.cmd";
    public static final String WINRM_CMDARGS = "winrm.cmdArgs";

    /**
     * Create a configured client for tests.
     * @return A configured {@link WsmanCli}
     * @throws java.lang.Exception On exceptions
     */
    WsmanCli createClient() throws Exception;
}

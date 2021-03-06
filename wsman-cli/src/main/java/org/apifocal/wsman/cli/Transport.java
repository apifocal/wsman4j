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

public enum Transport 
{
    PLAINTEXT, 
    SSL, 
    KERBEROS;

    static {
        PLAINTEXT.port = 9985;
        PLAINTEXT.scheme = "http";

        SSL.port = 5986;
        SSL.scheme = "https";
    }

    private int port;
    private String scheme;

    public int getPort() {
        return port;
    }
    public String getScheme() {
        return scheme;
    }

};


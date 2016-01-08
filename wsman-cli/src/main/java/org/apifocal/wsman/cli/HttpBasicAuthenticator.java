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

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.http.HTTPConduit;


/**
 * Authenticator using HTTP Basic specs.
 */
public class HttpBasicAuthenticator implements Authenticator {
    private AuthorizationPolicy auth;

    public HttpBasicAuthenticator(String username, String password) {
        // like the spring beans at http://cxf.apache.org/docs/client-http-transport-including-ssl-support.html
        auth = new AuthorizationPolicy();
        auth.setUserName(username);
        auth.setPassword(password);
        auth.setAuthorizationType("Basic"); // FIXME: use a CXF constant instead
    }

    @Override
    public void setupAuth(Client client) {
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setAuthorization(auth);
    }

}

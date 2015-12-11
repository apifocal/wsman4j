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

import java.util.Map;
import javax.xml.ws.BindingProvider;

public enum Transport 
{
    plaintext, 
    ssl, 
    kerberos
};

interface ITransport
{
    public void setupAuth(BindingProvider bindingProvider);
}

class BasicAuth implements ITransport
{
    private final String username;
    private final String password;
    
    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;        
    }
    
    @Override
    public void setupAuth(BindingProvider bindingProvider) {
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);                
        
        //HTTP headers
//       Map<String, List<String>> headers = new HashMap<String, List<String>>();
//       requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);        
    }    
}

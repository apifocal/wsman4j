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
package com.apifocal.wsman.cli;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.WSMAN;
import org.xmlsoap.schemas.ws._2004._09.transfer.AnyXmlType;
import org.dmtf.schemas.wbem.wsman._1.wsman.Shell;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * entry point
 */
public class Main {

    //@see https://stackoverflow.com/questions/15350367/apache-cxf-wdsl2java-code-generation-without-service-element
    protected static <T> T getService(final Class<T> serviceClass, String endpoint, final boolean useSoap12) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceClass);
        factory.setAddress(endpoint);
        if (useSoap12) {
            factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");
        }
        return serviceClass.cast(factory.create());
    }
    
    public static void main(String[] args) throws JAXBException {
        final String endpointAddress = "http://localhost:9985/wsman";
        
        WSMAN wsmanService = Main.getService(WSMAN.class, endpointAddress, true);
        
        //add SOAP headers, @see http://www.sachinhandiekar.com/2014/02/setting-soap-headers-in-apache-cxf.html
        List<Header> headersList = new ArrayList<>();
//        headersList.add(
//            new Header(new QName("http://sachinhandiekar.com/ws/SampleWS", "userName"), "JohnDoe", new JAXBDataBinding(String.class))
//        );
        Client proxy = ClientProxy.getClient(wsmanService);        
        proxy.getRequestContext().put(Header.HEADER_LIST, headersList);
        
        //invoke wsman service's 'create'
        //Shell shell = new Shell();
        AnyXmlType body = new AnyXmlType();
        //body.setAny(shell);
        wsmanService.create(body); //ws call
    }    
}

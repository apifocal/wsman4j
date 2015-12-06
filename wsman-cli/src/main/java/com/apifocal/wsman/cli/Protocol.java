package com.apifocal.wsman.cli;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.WSMAN;
import org.xmlsoap.schemas.ws._2004._09.transfer.AnyXmlType;

/**
 * wsman soap protocol
 */
public class Protocol {
    
    private WSMAN wsmanService;

    Protocol(URL url, EnumTransport transport) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(WSMAN.class);
        factory.setAddress(url.toString());
        //factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");
        wsmanService = WSMAN.class.cast(factory.create());
    }

    int openShell() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //wsmanService.create(body);

        
//        //add SOAP headers
//        List<Header> headersList = new ArrayList<>();
////        headersList.add(
////            new Header(new QName("http://sachinhandiekar.com/ws/SampleWS", "userName"), "JohnDoe", new JAXBDataBinding(String.class))
////        );
//        Client proxy = ClientProxy.getClient(wsmanService);
//        proxy.getRequestContext().put(Header.HEADER_LIST, headersList);
//
//        //add SOAP body
//        AnyXmlType body = new AnyXmlType();
//        //Shell shell = new Shell();
//        //body.setAny(shell);
//
//        //invoke 'create' ws
//
//        return 0;
    }

    int runCommand(int shellId, String command, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //wsmanService.command(body);
    }

    Response getCommandOutput(int shellId, int commandId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //wsmanService.receive(body);
    }

    void cleanupCommand(int shellId, int commandId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //wsmanService.signal(body);
    }

    void closeShell(int shellId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //wsmanService.delete();
    }    
}

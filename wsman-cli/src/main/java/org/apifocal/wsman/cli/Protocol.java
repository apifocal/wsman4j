package org.apifocal.wsman.cli;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.dmtf.schemas.wbem.wsman._1.wsman.CommandResponse;
import org.dmtf.schemas.wbem.wsman._1.wsman.Shell;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.AttributableDuration;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.AttributableURI;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.Locale;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.MaxEnvelopeSizeType;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.WSMAN;
import org.xmlsoap.schemas.ws._2004._08.addressing.AttributedURI;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;

/**
 * wsman soap protocol
 */
public class Protocol {

    //private final URL url;
    private final WSMAN wsmanService;

    private static final String URI_ACTION_CREATE = "http://schemas.xmlsoap.org/ws/2004/09/transfer/Create";
    private static final String URI_ROLE_ANONYMOUS = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous";
    private static final String URI_WSMAN_XSD = "http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd";
    private static final String URI_SHELL_CMD = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/cmd";
    private static final String MUST_UNDERSTAND = "mustUnderstand";

    Protocol(URL url, Transport transport) {
        //this.url = url;
        
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(WSMAN.class);
        factory.setAddress(url.toString());
        //factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");
        wsmanService = WSMAN.class.cast(factory.create());
    }

    int openShell() {
        //add SOAP headers
        List<Header> soapHeaders = getSOAPHeaders(URI_ACTION_CREATE);
        Client proxy = ClientProxy.getClient(wsmanService);
        proxy.getRequestContext().put(Header.HEADER_LIST, soapHeaders);

        //add SOAP body
        Shell shell = new Shell();
        shell.getOutputStreams().add("stdout stderr");
        shell.getInputStreams().add("stdin");

        //ws call
        wsmanService.create(shell);

        return 0;
    }

    int runCommand(int shellId, String command, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //wsmanService.command(body);
    }

    CommandResponse getCommandOutput(int shellId, int commandId) {
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
    
    private List<Header> getSOAPHeaders(String actionURI) {
        List<Header> headersList = new ArrayList<>();

        try {
            //factory for xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing"
            org.xmlsoap.schemas.ws._2004._08.addressing.ObjectFactory af = new org.xmlsoap.schemas.ws._2004._08.addressing.ObjectFactory();
            
            //a:Action
            AttributedURI a = new AttributedURI();
                a.setValue(actionURI);
                a.getOtherAttributes().put(new QName("", MUST_UNDERSTAND), "true");
            JAXBElement<AttributedURI> action = af.createAction(a);
            headersList.add(new Header(action.getName(), action, new JAXBDataBinding(AttributedURI.class)));
            
            //a:MessageID
            AttributedURI m = new AttributedURI();
                m.setValue(String.format("uuid:%s", UUID.randomUUID().toString()));
            JAXBElement<AttributedURI> messageID = af.createMessageID(m);
            headersList.add(new Header(messageID.getName(), messageID, new JAXBDataBinding(AttributedURI.class)));
            
            //a:ReplyTo
            EndpointReferenceType e = new EndpointReferenceType();
                AttributedURI address = new AttributedURI();
                address.setValue(URI_ROLE_ANONYMOUS);
                address.getOtherAttributes().put(new QName("", MUST_UNDERSTAND), "true");
            e.setAddress(address);            
            JAXBElement<EndpointReferenceType> replyTo = af.createReplyTo(e);
            headersList.add(new Header(replyTo.getName(), replyTo, new JAXBDataBinding(EndpointReferenceType.class)));
            
            //a:To
            AttributedURI t = new AttributedURI();
                t.setValue("http://windows-host:5985/wsman");
            JAXBElement<AttributedURI> to = af.createTo(t);
            headersList.add(new Header(to.getName(), to, new JAXBDataBinding(AttributedURI.class)));

            //factory for xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd" @TODO@
            org.dmtf.schemas.wbem.wsman._1.wsman_xsd.ObjectFactory wf = new org.dmtf.schemas.wbem.wsman._1.wsman_xsd.ObjectFactory();            
            
            //w:MaxEnvelopeSize
            MaxEnvelopeSizeType s = new MaxEnvelopeSizeType();
                s.setValue(BigInteger.valueOf(153600));
                s.getOtherAttributes().put(new QName("", MUST_UNDERSTAND), "true");
            JAXBElement<MaxEnvelopeSizeType> maxEnvelopeSize = wf.createMaxEnvelopeSize(s);
            headersList.add(new Header(maxEnvelopeSize.getName(), maxEnvelopeSize, new JAXBDataBinding(MaxEnvelopeSizeType.class)));
            
            //w:Locale
            Locale l = wf.createLocale();
                l.setLang("en-US");
                l.getOtherAttributes().put(new QName("", MUST_UNDERSTAND), "true");
            JAXBElement<Locale> locale = new JAXBElement<>(new QName(URI_WSMAN_XSD, "Locale", "w"), Locale.class, l);
            headersList.add(new Header(locale.getName(), locale, new JAXBDataBinding(Locale.class)));

            //w:OperationTimeout
            AttributableDuration ot = new AttributableDuration();
                ot.setValue(DatatypeFactory.newInstance().newDuration("PT60S"));
            JAXBElement<AttributableDuration> operationTimeout = wf.createOperationTimeout(ot);
            headersList.add(new Header(operationTimeout.getName(), operationTimeout, new JAXBDataBinding(AttributableDuration.class)));

            //w:ResourceURI
            AttributableURI r = new AttributableURI();
                r.setValue(URI_SHELL_CMD);
                r.getOtherAttributes().put(new QName("", MUST_UNDERSTAND), "true");            
            JAXBElement<AttributableURI> resourceURI = wf.createResourceURI(r);
            headersList.add(new Header(resourceURI.getName(), resourceURI, new JAXBDataBinding(AttributableURI.class)));
            
            //factory for xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd" @TODO@
            //com.microsoft.schemas.wbem.wsman._1.wsman_xsd.ObjectFactory pf = new com.microsoft.schemas.wbem.wsman._1.wsman_xsd.ObjectFactory();
            //p:DataLocale
            
        } catch (JAXBException | DatatypeConfigurationException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return headersList;
    }
}

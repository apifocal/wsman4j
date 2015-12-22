package org.apifocal.wsman.cli;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.microsoft.schemas.wbem.wsman._1.windows.shell.*;
//import org.dmtf.schemas.wbem.wsman._1.wsman.CommandLine;
//import org.dmtf.schemas.wbem.wsman._1.wsman.CommandResponse;
//import org.dmtf.schemas.wbem.wsman._1.wsman.DesiredStreamType;
//import org.dmtf.schemas.wbem.wsman._1.wsman.Receive;
//import org.dmtf.schemas.wbem.wsman._1.wsman.ReceiveResponse;
//import org.dmtf.schemas.wbem.wsman._1.wsman.Shell;
//import org.dmtf.schemas.wbem.wsman._1.wsman.Signal;
//import org.dmtf.schemas.wbem.wsman._1.wsman.SignalResponse;
//import org.dmtf.schemas.wbem.wsman._1.wsman.StreamType;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.AttributableDuration;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.AttributableURI;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.Locale;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.MaxEnvelopeSizeType;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.OptionSet;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.OptionType;
import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.SelectorType;
//import org.dmtf.schemas.wbem.wsman._1.wsman_xsd.WSMAN;
import org.xmlsoap.schemas.ws._2004._08.addressing.AttributedURI;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;
import org.xmlsoap.schemas.ws._2004._09.transfer.AnyXmlType;
import org.xmlsoap.schemas.ws._2004._09.transfer.CreateResponseType;

/**
 * wsman SOAP protocol
 *
 * the original wsdl (@see
 * https://msdn.microsoft.com/en-us/library/dd366131.aspx) had to be adjusted,
 * in order to include Shell element, which was missing
 */
public class Protocol {

    final WSMAN wsmanService;
    final ITransport transport;

    static final String URI_ACTION_CREATE = "http://schemas.xmlsoap.org/ws/2004/09/transfer/Create";
    static final String URI_ACTION_DELETE = "http://schemas.xmlsoap.org/ws/2004/09/transfer/Delete";
    static final String URI_ROLE_ANONYMOUS = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous";
    static final String URI_WSMAN_XSD = "http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd";
    static final String URI_RESOURCE_SHELL_CMD = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/cmd";
    static final String URI_ACTION_SHELL_COMMAND = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Command";
    static final String URI_ACTION_SHELL_RECEIVE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Receive";
    static final String URI_ACTION_SHELL_SIGNAL = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Signal";
    static final String URI_SHELL_CODE_TERMINATE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/signal/terminate";
    static final String COMMAND_STATE_DONE = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done";
    //static final String COMMAND_STATE_RUNNING = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Running";
    static final String MUST_UNDERSTAND = "mustUnderstand";

    public Protocol(URL url, ITransport transport) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(WSMAN.class);
        factory.setAddress(url.toString());
        factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");
        wsmanService = WSMAN.class.cast(factory.create());

        this.transport = transport;
    }

    //create a wsman Shell instance; @see https://msdn.microsoft.com/en-us/library/cc251739.aspx
    public String openShell() {
        final boolean noprofile = false; //@TODO@ make param
        final int codepage = 437; //@TODO@ make param

        String messageId = UUID.randomUUID().toString();
        HashMap<String, String> options = new HashMap<>();
        options.put("WINRS_NOPROFILE", noprofile ? "TRUE" : "FALSE");
        options.put("WINRS_CODEPAGE", String.valueOf(codepage));

        prepareRequest(URI_ACTION_CREATE, null, messageId, options);

        //add SOAP body
        Shell shell = new Shell();
        shell.getOutputStreams().add("stdout stderr");
        shell.getInputStreams().add("stdin");
//        shell.setEnvironment();
//        shell.setIdleTimeout();
//        shell.setWorkingDirectory();

        //ws call
        CreateResponseType response = wsmanService.create(shell);
        
        Shell sh = (Shell)response.getAny();
        return sh.getShellId(); //@TODO@ get shellId (from response)
    }

    public String runCommand(String shellId, String command, String[] args) {
        final boolean consoleModeStdin = true;
        final boolean skipCmdShell = false;

        String messageId = UUID.randomUUID().toString();
        HashMap<String, String> options = new HashMap<>();
        options.put("WINRS_CONSOLEMODE_STDIN", consoleModeStdin ? "TRUE" : "FALSE");
        options.put("WINRS_SKIP_CMD_SHELL", skipCmdShell ? "TRUE" : "FALSE");

        prepareRequest(URI_ACTION_SHELL_COMMAND, shellId, messageId, options);

        //add SOAP body
        CommandLine theCommand = new CommandLine();
        theCommand.setCommand(command);
        theCommand.getArguments().addAll(Arrays.asList(args));

        //ws call
        CommandResponse response = wsmanService.command(theCommand);

        return response.getCommandId();
    }

    public CommandOutput getCommandOutput(String shellId, String commandId) {
        String messageId = UUID.randomUUID().toString();
        HashMap<String, String> options = null; //new HashMap<>();

        prepareRequest(URI_ACTION_SHELL_RECEIVE, shellId, messageId, options);

        //add SOAP body
        Receive recv = new Receive();
        DesiredStreamType stream = new DesiredStreamType();
        stream.setCommandId(commandId);
        stream.getValue().add("stdout stderr");
        recv.setDesiredStream(stream);

        CommandOutput commandOutput = new CommandOutput();

        //fetch output in a loop until command finishes
        while (true) {
            ReceiveResponse response = wsmanService.receive(recv); //ws call

            for (StreamType streamItem : response.getStream()) {
                if (streamItem.getName().equals("stdout")) {
                    try {
                        commandOutput.std_out += Base64Utility.decode(new String(streamItem.getValue()));
                    } catch (Base64Exception ex) {
                        Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if (streamItem.getName().equals("stderr")) {
                    try {
                        commandOutput.std_err += Base64Utility.decode(new String(streamItem.getValue()));
                    } catch (Base64Exception ex) {
                        Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            //quit loop when command output says 'done'
            if (response.getCommandState().getState().equals(COMMAND_STATE_DONE)) {
                commandOutput.statusCode = response.getCommandState().getExitCode().intValue();
                break;
            }
        }

        return commandOutput;
    }

    public void cleanupCommand(String shellId, String commandId) {
        String messageId = UUID.randomUUID().toString();
        HashMap<String, String> options = null; //new HashMap<>();

        prepareRequest(URI_ACTION_SHELL_SIGNAL, shellId, messageId, options);

        //add SOAP body
        Signal signal = new Signal();
        signal.setCommandId(commandId);
        signal.setCode(URI_SHELL_CODE_TERMINATE);

        //ws call
        SignalResponse response = wsmanService.signal(signal);
    }

    public void closeShell(String shellId) {
        String messageId = UUID.randomUUID().toString();
        HashMap<String, String> options = null; //new HashMap<>();

        prepareRequest(URI_ACTION_DELETE, shellId, messageId, options);

        //no SOAP body
        AnyXmlType response = wsmanService.delete();
    }

    void prepareRequest(String actionURI, String shellId, String messageId, HashMap<String, String> options) {
        //add SOAP headers
        List<Header> soapHeaders = getSOAPHeaders(actionURI, URI_RESOURCE_SHELL_CMD, shellId, messageId, options);

        Client proxy = ClientProxy.getClient(wsmanService);
        proxy.getRequestContext().put(Header.HEADER_LIST, soapHeaders);
        proxy.getRequestContext().put("SOAPAction", actionURI);

        transport.setupAuth(proxy);
    }

    List<Header> getSOAPHeaders(String actionURI, String resourceURI, String shellId, String messageId, HashMap<String, String> options) {
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
            m.setValue(String.format("uuid:%s", messageId));
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
            r.setValue(resourceURI);
            r.getOtherAttributes().put(new QName("", MUST_UNDERSTAND), "true");
            JAXBElement<AttributableURI> resource = wf.createResourceURI(r);
            headersList.add(new Header(resource.getName(), resource, new JAXBDataBinding(AttributableURI.class)));

            //w:Selector
            if (shellId != null) {
                SelectorType sel = new SelectorType();
                sel.setName(shellId);
                JAXBElement<SelectorType> selector = wf.createSelector(sel);
                headersList.add(new Header(selector.getName(), selector, new JAXBDataBinding(SelectorType.class)));
            }

            //w:OptionSet
            if (!options.isEmpty()) {
                OptionSet opt = wf.createOptionSet();

                for (Entry<String, String> entry : options.entrySet()) {
                    OptionType o = new OptionType();
                    o.setName(entry.getKey());
                    o.setValue(entry.getValue());

                    opt.getOption().add(o);
                }

                JAXBElement<OptionSet> optionsSet = new JAXBElement<>(new QName(URI_WSMAN_XSD, "OptionSet", "w"), OptionSet.class, opt);
                headersList.add(new Header(optionsSet.getName(), optionsSet, new JAXBDataBinding(OptionSet.class)));
            }

            //factory for xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd" @TODO@
            //com.microsoft.schemas.wbem.wsman._1.wsman_xsd.ObjectFactory pf = new com.microsoft.schemas.wbem.wsman._1.wsman_xsd.ObjectFactory();
            //p:DataLocale            
        } catch (JAXBException | DatatypeConfigurationException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, null, ex);
        }

        return headersList;
    }
}

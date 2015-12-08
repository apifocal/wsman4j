package com.apifocal.wsman.cli;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.cxf.common.util.Base64Utility;

/**
 * establish a wsman session using a {@link Protocol}
 */
public class Session {    
    Protocol protocol;

    public Session(URL url, Transport transport) {
        protocol = new Protocol(url, transport);
    }

    /* execute cmd instructions */
    public Response runCmd(String command, String... args) {
        int shellId = protocol.openShell();
        int commandId = protocol.runCommand(shellId, command, args);
        Response rs = protocol.getCommandOutput(shellId, commandId);
        protocol.cleanupCommand(shellId, commandId);
        protocol.closeShell(shellId);
        return rs;
    }
    
    /* encodes a Powershell script using base64 and executes the encoded script */
    public Response runPs(String script) {
        //TODO must use utf16 little endian on windows
        String base64Script = Base64Utility.encode(script.getBytes(StandardCharsets.UTF_8));
        String command = String.format("powershell -encodedcommand %s", base64Script);
        Response rs = runCmd(command);
        if (!rs.std_err.isEmpty()) //if there was an error message, clean it it up and make it human readable
            rs.std_err = cleanErrorMsg(rs.std_err);
        return rs;
    }
    
    /* converts a Powershell CLIXML message to a more human readable string */
    private String cleanErrorMsg(String std_err) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String stripNamespace(String xml) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

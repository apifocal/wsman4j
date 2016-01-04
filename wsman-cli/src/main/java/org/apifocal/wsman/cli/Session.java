package org.apifocal.wsman.cli;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.cxf.common.util.Base64Utility;

/**
 * establish a wsman session using a {@link Protocol}
 */
public class Session {

    private final Protocol protocol;

    public Session(URL url, ITransport transport) {
        protocol = new Protocol(url, transport);
    }

    /* execute cmd instructions */
    public CommandOutput runCmd(String command, String... args) {
        String shellId = protocol.openShell();
        String commandId = protocol.runCommand(shellId, command, args);
        CommandOutput out = protocol.getCommandOutput(shellId, commandId);
        protocol.cleanupCommand(shellId, commandId);
        protocol.closeShell(shellId);
        return out;
    }

    /* execute a Powershell script (first encode it using base64) */
    public CommandOutput runPs(String script) {
        //@TODO@ must use utf16 little endian on windows
        String base64Script = Base64Utility.encode(script.getBytes(StandardCharsets.UTF_8));
        String command = String.format("powershell -encodedcommand %s", base64Script);
        return runCmd(command);
    }
}

package me.prexorjustin.prexornetwork.cloud.driver.terminal.streams;

import lombok.RequiredArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node.PacketInSendConsole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class LoggerOutputStream extends ByteArrayOutputStream {

    private final Type type;

    @Override
    public void flush() throws IOException {
        final String string = this.toString(StandardCharsets.UTF_8);
        this.reset();

        if (string != null && !string.isEmpty()) {
            if (Driver.getInstance().getMessageStorage().isPrintConsoleToManager()) {
                NettyDriver.getInstance().getNettyClient().sendPacketAsynchronous(new PacketInSendConsole(
                        Driver.getInstance().getMessageStorage().getPrintConsoleToManagerName(), string
                ));
            }

            Driver.getInstance().getTerminalDriver().log(this.type, string.split("\n"));
            Driver.getInstance().getTerminalDriver().getLineReader().getTerminal().flush();
        }
    }
}

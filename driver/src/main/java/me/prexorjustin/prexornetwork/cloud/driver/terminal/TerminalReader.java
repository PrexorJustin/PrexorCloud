package me.prexorjustin.prexornetwork.cloud.driver.terminal;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

public class TerminalReader extends Thread {

    private final TerminalDriver terminalDriver;
    private final CommandDriver commandDriver;

    public TerminalReader(TerminalDriver terminalDriver) {
        this.terminalDriver = terminalDriver;
        this.commandDriver = terminalDriver.getCommandDriver();
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                String prompt = String.format("§bPrexorCloud§f@%s §7=> ", System.getProperty("user.name"));
                final String line = this.terminalDriver.getLineReader().readLine(this.terminalDriver.getColoredString(prompt));

                if (line != null && !line.trim().isEmpty()) {
                    final TerminalStorageLine input = this.terminalDriver.getInputs().poll();

                    if (Driver.getInstance().getMessageStorage().isOpenServiceScreen())
                        Driver.getInstance().getMessageStorage().getConsoleInput().add(line);
                    else if (this.terminalDriver.isInSetup()) handleSetupInput(line);
                    else if (input != null) input.inputs().accept(line);
                    else this.commandDriver.executeCommand(line);

                } else
                    this.terminalDriver.log(
                            Type.COMMAND,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-not-found")
                    );

            } catch (UserInterruptException exception) {
                this.terminalDriver.log(
                        Type.COMMAND,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-error")
                );
            } catch (EndOfFileException exception) {
                if (this.terminalDriver.getSetupDriver().getSetup() != null) this.terminalDriver.leaveSetup();
                else this.commandDriver.executeCommand("stop");
            } catch (UnsupportedOperationException exception) {
                interrupt();
                System.exit(0);
            }
        }
    }

    private void handleSetupInput(String line) {
        if (line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("leave ")) this.terminalDriver.leaveSetup();
        else Driver.getInstance().getTerminalDriver().getSetupDriver().getSetup().call(line.replace(" ", ""));
    }
}

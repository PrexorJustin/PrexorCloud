package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

@CommandInfo(command = "queue", description = "command-queue-description", aliases = {"servicequeue", "waitingline"})
public class QueueCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        ConcurrentLinkedDeque<String> start = PrexorCloudManager.queueDriver.getStartupQueue();
        ConcurrentLinkedDeque<String> stop = PrexorCloudManager.queueDriver.getShutdownQueue();

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (start.isEmpty() && stop.isEmpty()) {
                Driver.getInstance().getTerminalDriver().log(
                        Type.COMMAND,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-is-empty")
                );
                return;
            }

            Driver.getInstance().getTerminalDriver().log(
                    Type.COMMAND,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-service-in-queue")
            );

            Driver.getInstance().getTerminalDriver().log(Type.EMPTY);
            start.forEach(s -> Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                    " >> " + s + "| START"));
            stop.forEach(s -> Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                    " >> " + s + " | STOP"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            String service = args[1];
            if (stop.stream().noneMatch(s -> s.equalsIgnoreCase(service)) && start.stream().noneMatch(s -> s.equals(service))) {
                Driver.getInstance().getTerminalDriver().log(
                        Type.COMMAND,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-service-not-in-queue")
                );
            } else if (stop.stream().anyMatch(s -> s.equalsIgnoreCase(service))) {
                PrexorCloudManager.queueDriver.getShutdownQueue().removeIf(s -> s.equalsIgnoreCase(service));

                Driver.getInstance().getTerminalDriver().log(
                        Type.COMMAND,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-service-remove")
                );
            } else if (start.stream().anyMatch(s -> s.equalsIgnoreCase(service))) {
                PrexorCloudManager.queueDriver.getStartupQueue().removeIf(s -> s.equalsIgnoreCase(service));
                PrexorCloudManager.serviceDriver.unregistered(service);

                Driver.getInstance().getTerminalDriver().log(
                        Type.COMMAND,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-service-remove")
                );
            } else sendHelp();
        } else sendHelp();
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> commands = new ArrayList<>();
        if (args.length == 0) {
            commands.add("remove");
            commands.add("list");
        }
        return commands;
    }

    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-help-1"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-queue-help-2")
        );
    }
}

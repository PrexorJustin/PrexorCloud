package me.prexorjustin.prexornetwork.cloud.driver.terminal.completer;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TerminalCompleter implements Completer {

    private boolean exists;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String line = parsedLine.line();
        List<String> suggestions = null;

        if (Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
            LinkedList<String> results = new LinkedList<>();
            results.add("leave");
            suggestions = results;
            suggestions.stream().map(Candidate::new).forEach(list::add);
        } else if (Driver.getInstance().getTerminalDriver().isInSetup()) {
            LinkedList<String> results = new LinkedList<>();
            results.addAll(
                    Driver.getInstance().getTerminalDriver().getSetupDriver().getSetup().tabComplete() != null
                            ? Driver.getInstance().getTerminalDriver().getSetupDriver().getSetup().tabComplete()
                            : new ArrayList<>()
            );

            suggestions = results;
            suggestions.stream().map(Candidate::new).forEach(list::add);
        } else {
            if (line.isEmpty() || !canBeFound(line)) {
                LinkedList<String> results = new LinkedList<>();
                Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().forEach(commandAdapter -> {
                    results.add(commandAdapter.getCommand());
                    results.addAll(commandAdapter.getAliases());
                });

                suggestions = results;
                suggestions.stream().map(Candidate::new).forEach(list::add);
            } else {
                String[] arguments = line.split(" ");
                TerminalStorageLine consoleInput = Driver.getInstance().getTerminalDriver().getInputs().peek();

                if (line.indexOf(' ') == -1) {
                    if (consoleInput == null) {
                        LinkedList<String> results = new LinkedList<>();
                        String toTest = arguments[arguments.length - 1];

                        Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().forEach(commandAdapter -> {
                            if (commandAdapter.getCommand() != null && (toTest.trim().isEmpty() || commandAdapter.getCommand().toLowerCase().contains(toTest.toLowerCase())))
                                results.add(commandAdapter.getCommand());

                            commandAdapter.getAliases().forEach(alias -> {
                                if (alias.toLowerCase().contains(toTest.toLowerCase())) results.add(alias);
                            });
                        });

                        if (results.isEmpty() && !Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().isEmpty()) {
                            Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().forEach(commandAdapter -> {
                                results.add(commandAdapter.getCommand());
                                results.addAll(commandAdapter.getAliases());
                            });
                        }

                        suggestions = results;
                    } else {
                        suggestions = consoleInput.tabCompletes();
                    }
                } else {
                    if (consoleInput != null) return;
                    CommandAdapter command = Driver.getInstance().getTerminalDriver().getCommandDriver().getCommand(arguments[0]);
                    LinkedList<String> results = new LinkedList<>();

                    if (command == null) {
                        Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().forEach(commandAdapter -> {
                            results.add(commandAdapter.getCommand());
                            results.addAll(commandAdapter.getAliases());
                        });
                    } else {
                        if (command.tabComplete(consoleInput, arguments) != null) {
                            results.addAll(command.tabComplete(consoleInput, Driver.getInstance().getMessageStorage().dropFirstString(arguments)));
                            suggestions = results;
                        }
                    }
                    if (suggestions == null || suggestions.isEmpty()) return;
                }

                suggestions.stream().map(Candidate::new).forEach(list::add);
            }
        }
    }

    private boolean canBeFound(String line) {
        ArrayList<String> commands = new ArrayList<>();
        this.exists = false;

        if (line.contains(" ")) return true;

        Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().forEach(commandAdapter -> {
            commands.add(commandAdapter.getCommand());
            commands.addAll(commandAdapter.getAliases());
        });

        commands.forEach(command -> {
            if (command.startsWith(line)) this.exists = true;
        });

        return this.exists;
    }
}

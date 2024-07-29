package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.ArrayList;

@CommandInfo(command = "cloud", description = "command-me-description", aliases = {"me", "prexorcloud", "info"})
public class PrexorCloudCommand extends CommandAdapter {
    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double usedMemory = (double) memoryMXBean.getHeapMemoryUsage().getUsed() / 1048576;
        double maxMemory = (double) memoryMXBean.getHeapMemoryUsage().getMax() / 1048576;
        int processors = operatingSystemMXBean.getAvailableProcessors();
        int loadedClassCount = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long totalLoadedClassCount = ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount();
        double prozent = (usedMemory * 100) / maxMemory;
        DecimalFormat f = new DecimalFormat("#0.00");
        DecimalFormat f2 = new DecimalFormat("#0");

        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                "Version:§f PrexorCloud-" + Driver.getInstance().getMessageStorage().getVersion(),
                "Author: §fPrexorJustin",
                "Website: §fWORK IN PROGRESS",
                "",
                "OS Version: §f" + System.getProperty("os.name"),
                "User: §f" + System.getProperty("user.name"),
                "Java version: §f" + System.getProperty("java.version"),
                "Memory: §f" + f2.format(usedMemory) + "MB§7/§f" + f2.format(maxMemory) + "MB §7(§f" + f.format(prozent) + "%§7)",
                "Cores: §f" + processors,
                "Current Services: §f" + PrexorCloudManager.serviceDriver.getServices().size(),
                "Uptime: §f" + uptime,
                "Loaded Classes: §f" + loadedClassCount,
                "Totale Classes: §f" + totalLoadedClassCount);
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        return new ArrayList<>();
    }
}

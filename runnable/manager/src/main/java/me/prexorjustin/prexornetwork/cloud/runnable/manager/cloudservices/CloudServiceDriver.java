package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.CloudPlayerRestCache;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyLaunchEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceLaunchEvent;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.storage.uuid.UUIDDriver;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServices;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player.PlayerGeneral;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceConnected;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceLaunch;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudProxyCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudServiceCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.NetworkEntry;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedEntry;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedService;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.interfaces.ICloudServiceDriver;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CloudServiceDriver implements ICloudServiceDriver {

    private final ArrayDeque<TaskedService> services;
    public final ArrayDeque<String> delete;
    public final NetworkEntry entry;

    public CloudServiceDriver() {
        this.entry = new NetworkEntry();
        this.services = new ArrayDeque<>();
        this.delete = new ArrayDeque<>();

        this.handleServices();
    }

    @Override
    public TaskedService register(TaskedEntry entry) {
        if (getService(entry.getServiceName()) != null) {
            if (!entry.getServiceName().endsWith("0")) return getService(entry.getServiceName());
            return null;
        } else if (entry.getServiceName().endsWith("0")) return null;
        else {
            if (!this.entry.getGroupPlayerPotency().containsKey(entry.getGroupName()))
                this.entry.getGroupPlayerPotency().put(entry.getGroupName(), 0);

            TaskedService service = new TaskedService(entry);
            services.add(service);

            PrexorCloudManager.queueDriver.addQueuedObjectToStart(entry.getServiceName());

            return service;
        }
    }

    @Override
    public void unregister(String service) {
        if (getService(service) == null) return;

        if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(service))
            NettyDriver.getInstance().getNettyServer().removeChannel(service);

        PrexorCloudManager.serviceDriver.getService(service).getEntry().setServiceState(ServiceState.QUEUED);
        PrexorCloudManager.queueDriver.addQueuedObjectToShutdown(service);
    }

    @Override
    public void unregistered(String service) {
        if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(service))
            NettyDriver.getInstance().getNettyServer().removeChannel(service);

        int memory = Driver.getInstance().getGroupDriver().load(getService(service).getEntry().getGroupName()).getUsedMemory();
        Driver.getInstance().getMessageStorage().setCanUseMemory(Driver.getInstance().getGroupDriver().load(getService(service).getEntry().getGroupName()).getUsedMemory() + memory);

        services.removeIf(taskedService -> taskedService.getEntry().getServiceName().equals(service));
    }

    @Override
    public Integer getFreeUUID(String group) {
        return Optional.ofNullable(Driver.getInstance().getGroupDriver().load(group)).map(gs -> IntStream.range(1, gs.getMaxOnline() == -1 ? Integer.MAX_VALUE : gs.getMaxOnline() + 1).filter(i -> !getServices(group).stream().map(s -> Integer.parseInt(s.getEntry().getServiceName().replace(group, "").replace(PrexorCloudManager.config.getSplitter(), ""))).toList().contains(i)).findFirst().orElse(0)).orElse(0);
    }

    @Override
    public String getFreeUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Integer getActiveServices(String group) {
        return (int) getServices(group).stream().filter(service -> service.getEntry().getServiceState() == ServiceState.LOBBY || service.getEntry().getServiceState() == ServiceState.QUEUED || service.getEntry().getServiceState() == ServiceState.STARTED).count();
    }

    @Override
    public Integer getLobbiedServices(String group) {
        return (int) getServices(group).stream().filter(service -> service.getEntry().getServiceState() == ServiceState.LOBBY && !service.hasStartedNew()).count();
    }

    @Override
    public Integer getFreePort(boolean proxy) {
        return IntStream.range(proxy ? PrexorCloudManager.config.getBungeePort() : PrexorCloudManager.config.getSpigotPort(), Integer.MAX_VALUE).filter(p -> !getServices().stream().map(TaskedService::getEntry).filter(sEntry -> sEntry.getTaskNode().equals("InternalNode")).map(TaskedEntry::getUsedPort).toList().contains(p)).findFirst().orElse(0);
    }

    @Override
    public void handleServices() {

        Thread current = new Thread(() -> {

            /*
              Handle QueueDriver
             */

            new TimerBase().scheduleAsync(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (Driver.getInstance().getMessageStorage().getCPULoad() <= PrexorCloudManager.config.getProcessorUsage()) {
                            int startedCount = getServices().stream().filter(ts -> ts.getEntry().getServiceState() == ServiceState.STARTED).toList().size();

                            if (startedCount <= PrexorCloudManager.config.getServiceStartupCount() && !PrexorCloudManager.queueDriver.getStartupQueue().isEmpty()) {
                                String service = PrexorCloudManager.queueDriver.getStartupQueue().removeFirst();
                                String groupName = PrexorCloudManager.serviceDriver.getService(service).getEntry().getGroupName();
                                String node = PrexorCloudManager.serviceDriver.getService(service).getEntry().getTaskNode();

                                Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(Driver.getInstance().getGroupDriver().load(groupName).getGroupType().equalsIgnoreCase("PROXY") ? new CloudProxyLaunchEvent(service, groupName, node) : new CloudServiceLaunchEvent(service, groupName, node));
                                NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutServiceLaunch(service, Driver.getInstance().getGroupDriver().load(groupName).getGroupType().equalsIgnoreCase("PROXY"), groupName, node));

                                PrexorCloudManager.serviceDriver.getService(service).handleStatusChange(ServiceState.STARTED);
                                PrexorCloudManager.serviceDriver.getService(service).handleLaunch();
                            }

                            if (!PrexorCloudManager.queueDriver.getShutdownQueue().isEmpty()) {
                                String service = PrexorCloudManager.queueDriver.getShutdownQueue().removeFirst();

                                PrexorCloudManager.serviceDriver.getService(service).handleQuit();
                                PrexorCloudManager.serviceDriver.unregistered(service);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }, 0, 100, TimeUtil.MILLISECONDS);

            /*
             Handle minimal Service count
             */

            new TimerBase().scheduleAsync(new TimerTask() {
                @Override
                public void run() {
                    if (PrexorCloudManager.shutdown) cancel();
                    ArrayList<Group> groups = Driver.getInstance().getGroupDriver().getAll();
                    ArrayDeque<TaskedService> currentServices = getServices();

                    entry.setGlobalPlayers(currentServices.stream().mapToInt(s -> s.getEntry().getCurrentPlayers()).sum());
                    entry.setGlobalPlayersPotency(entry.getGlobalPlayers() / 100);

                    HashMap<String, Integer> groupPlayerPotency = groups.parallelStream()
                            .map(group -> getServices(group.getGroupType()))
                            .flatMap(List::stream)
                            .filter(taskedService -> taskedService.getEntry().getCurrentPlayers() > 100)
                            .collect(
                                    Collectors.groupingBy(s -> s.getEntry().getGroupName(), Collectors.summingInt(s -> s.getEntry().getCurrentPlayers()))
                            )
                            .entrySet()
                            .stream()
                            .collect(
                                    HashMap::new,
                                    (map, entry) -> map.put(entry.getKey(), entry.getValue() / 100),
                                    HashMap::putAll
                            );

                    entry.getGroupPlayerPotency().putAll(groupPlayerPotency);
                }
            }, 5, 5, TimeUtil.SECONDS);

            /*
            Handle TaskedService's this includes things such as Filtering; Removing; Delays; Startup
             */

            new TimerBase().scheduleAsync(new TimerTask() {
                @Override
                public void run() {
                    if (!PrexorCloudManager.shutdown) {
                        List<TaskedService> services = getServices().stream()
                                .filter(taskedService -> taskedService.getEntry().getServiceState() == ServiceState.LOBBY)
                                .filter(taskedService -> taskedService.getEntry().getCurrentPlayers() == 0)
                                .filter(taskedService -> Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - taskedService.getEntry().getTime()))) >= 120)
                                .toList();

                        services.forEach(taskedService -> {
                            Group group = Driver.getInstance().getGroupDriver().load(taskedService.getEntry().getGroupName());
                            int online = (!entry.getGroupPlayerPotency().containsKey(group.getGroupType())) ? group.getMinOnline()
                                    : (entry.getGroupPlayerPotency().get(group.getGroupType()) == 0 && entry.getGlobalPlayersPotency() == 0) ? group.getMinOnline()
                                    : (entry.getGlobalPlayersPotency() != 0) ? group.getOver100AtNetwork() * entry.getGlobalPlayersPotency()
                                    : group.getOver100AtGroup() * entry.getGroupPlayerPotency().get(group.getGroupType());

                            if (taskedService.hasStartedNew()) unregister(taskedService.getEntry().getServiceName());
                            if (getLobbiedServices(taskedService.getEntry().getGroupName()) - 1 >= online)
                                unregister(taskedService.getEntry().getServiceName());
                        });

                        List<TaskedService> lobbyServices = getServices().stream()
                                .filter(taskedService -> taskedService.getEntry().getServiceState() == ServiceState.LOBBY)
                                .toList();

                        lobbyServices.forEach(taskedService -> {
                            Group group = Driver.getInstance().getGroupDriver().load(taskedService.getEntry().getGroupName());
                            int minOnline = (!entry.getGroupPlayerPotency().containsKey(group.getGroupType())) ? group.getMinOnline()
                                    : (entry.getGroupPlayerPotency().get(group.getGroupType()) == 0 && entry.getGlobalPlayers() == 0) ? group.getMinOnline()
                                    : (entry.getGlobalPlayersPotency() != 0) ? group.getOver100AtNetwork() * entry.getGlobalPlayersPotency()
                                    : group.getOver100AtGroup() * entry.getGroupPlayerPotency().get(group.getGroupType());

                            int inStoppedQueue = PrexorCloudManager.queueDriver.getShutdownQueue().stream()
                                    .filter(s -> getService(s).getEntry().getGroupName().equalsIgnoreCase(taskedService.getEntry().getGroupName()))
                                    .toList().size();

                            if (getLobbiedServices(taskedService.getEntry().getGroupName()) - 1 - inStoppedQueue >= minOnline)
                                unregister(taskedService.getEntry().getServiceName());
                        });

                        lobbyServices.stream()
                                .filter(taskedService -> !taskedService.hasStartedNew())
                                .filter(taskedService -> {
                                    Group group = Driver.getInstance().getGroupDriver().load(taskedService.getEntry().getGroupName());
                                    final double need_players = ((double) group.getMaxPlayer() / (double) 100) * (double) group.getStartNewPercentage();
                                    return taskedService.getEntry().getCurrentPlayers() >= (int) need_players && !taskedService.hasStartedNew();
                                }).forEach(taskedService -> {
                                    taskedService.hasStartedNew(true);
                                    String id = PrexorCloudManager.config.getUuid().equals("INT") ? String.valueOf(PrexorCloudManager.serviceDriver.getFreeUUID(taskedService.getEntry().getGroupName())) : getFreeUUID();
                                    if (taskedService.getEntry().getTaskNode().equals("InternalNode")) {
                                        PrexorCloudManager.serviceDriver.register(
                                                new TaskedEntry(
                                                        PrexorCloudManager.serviceDriver.getFreePort(Driver.getInstance().getGroupDriver().load(taskedService.getEntry().getGroupName()).getGroupType().equalsIgnoreCase("PROXY")),
                                                        taskedService.getEntry().getGroupName(),
                                                        taskedService.getEntry().getGroupName() + PrexorCloudManager.config.getSplitter() + id,
                                                        "InternalNode",
                                                        taskedService.getEntry().isUseProtocol(),
                                                        id,
                                                        false,
                                                        ""
                                                )
                                        );
                                    } else {
                                        PrexorCloudManager.serviceDriver.register(
                                                new TaskedEntry(
                                                        -1,
                                                        taskedService.getEntry().getGroupName(),
                                                        taskedService.getEntry().getGroupName() + PrexorCloudManager.config.getSplitter() + id,
                                                        taskedService.getEntry().getTaskNode(),
                                                        taskedService.getEntry().isUseProtocol(),
                                                        id,
                                                        false,
                                                        ""
                                                )
                                        );
                                    }
                                });
                    }
                }
            }, 0, 30, TimeUtil.SECONDS);

            /*
            Handle Service Startup's and Shutdown's
             */

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (PrexorCloudManager.shutdown) cancel();

                    Driver.getInstance().getGroupDriver().getAll().stream()
                            .filter(group -> (!entry.getGroupPlayerPotency().containsKey(group.getGroupType())) ? (getActiveServices(group.getGroupType()) < group.getMinOnline()) : ((entry.getGroupPlayerPotency().get(group.getGroupType()) == 0 && entry.getGlobalPlayersPotency() == 0) ? (getActiveServices(group.getGroupType()) < group.getMinOnline()) : ((entry.getGlobalPlayersPotency() != 0) ? (getActiveServices(group.getGroupType()) < group.getOver100AtNetwork() * entry.getGlobalPlayersPotency()) : (getActiveServices(group.getGroupType()) < group.getOver100AtGroup() * entry.getGroupPlayerPotency().get(group.getGroupType())))))
                            .filter(group -> getServices(group.getGroupType()).size() + 1 <= Integer.parseInt(String.valueOf(group.getMaxOnline()).replace("-1", String.valueOf(Integer.MAX_VALUE))))
                            .filter(group -> NettyDriver.getInstance().getNettyServer().isChannelRegistered(group.getStorage().getRunningNode()) || group.getStorage().getRunningNode().equals("InternalNode"))
                            .sorted(Comparator.comparingInt(Group::getStartPriority).reversed())
                            .forEach(group -> {
                                int online = (!entry.getGroupPlayerPotency().containsKey(group.getGroupType())) ? group.getMinOnline() : (entry.getGroupPlayerPotency().get(group.getGroupType()) == 0 && entry.getGlobalPlayersPotency() == 0) ? group.getMinOnline() : (entry.getGlobalPlayersPotency() != 0) ? group.getOver100AtNetwork() * entry.getGlobalPlayersPotency() : group.getOver100AtGroup() * entry.getGroupPlayerPotency().get(group.getGroupType());
                                if (!delete.contains(group.getGroupType())) {
                                    int minimal = online - getActiveServices(group.getGroupType());
                                    for (int i = 0; i != minimal; i++) {
                                        String id = PrexorCloudManager.config.getUuid().equals("INT") ? String.valueOf(PrexorCloudManager.serviceDriver.getFreeUUID(group.getGroupType())) : getFreeUUID();
                                        String entryName = group.getGroupType() + PrexorCloudManager.config.getSplitter() + id;
                                        String node = group.getStorage().getRunningNode();
                                        int freePort = getFreePort(group.getGroupType().equalsIgnoreCase("PROXY"));
                                        int memoryAfter = Driver.getInstance().getMessageStorage().getCanUseMemory() - group.getUsedMemory();

                                        if (node.equals("InternalNode") && memoryAfter >= 0) {
                                            PrexorCloudManager.serviceDriver.register(new TaskedEntry(freePort, group.getGroupType(), entryName, node, PrexorCloudManager.config.isUseProtocol(), id, false, ""));
                                            Driver.getInstance().getMessageStorage().setCanUseMemory(memoryAfter);
                                        } else if (!node.equals("InternalNode"))
                                            PrexorCloudManager.serviceDriver.register(new TaskedEntry(-1, group.getGroupType(), entryName, node, PrexorCloudManager.config.isUseProtocol(), id, false, ""));
                                    }
                                }
                            });
                }
            }, 0, 1000);


            /*
            Handle Service Crashes
             */

            new TimerBase().scheduleAsync(new TimerTask() {
                @Override
                public void run() {
                    if (!PrexorCloudManager.shutdown) {
                        PrexorCloudManager.serviceDriver.getServices().parallelStream().filter(taskedService -> taskedService.getEntry().getServiceState() != ServiceState.QUEUED && taskedService.getEntry().getServiceState() != ServiceState.STARTED).forEach(taskedService -> {
                            String route = WebServer.Routes.CLOUDSERVICE.getRoute().replace("%servicename%", taskedService.getEntry().getServiceName().replace(PrexorCloudManager.config.getSplitter(), "~"));
                            if (Driver.getInstance().getWebServer().doesContentExist(route)) {
                                LiveServices liveServices = (LiveServices) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute(route), LiveServices.class);
                                if (liveServices != null && liveServices.getLastReaction() != -1 && Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - liveServices.getLastReaction()))) >= PrexorCloudManager.config.getTimeoutCheck()) {
                                    unregister(taskedService.getEntry().getServiceName());
                                }
                            }
                        });
                        PrexorCloudManager.serviceDriver.getServices().parallelStream().filter(taskedService -> taskedService.getEntry().getServiceState() != ServiceState.QUEUED && taskedService.getEntry().getServiceState() == ServiceState.STARTED).toList().forEach(taskedService -> {
                            if (taskedService.getProcess() != null && taskedService.getProcess().getProcess() != null && !taskedService.getProcess().getProcess().isAlive()) {
                                unregister(taskedService.getEntry().getServiceName());
                                if (Driver.getInstance().getGroupDriver().load(taskedService.getEntry().getGroupName()).getGroupType().equalsIgnoreCase("PROXY")) {
                                    Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudProxyCouldNotStartEvent(taskedService.getEntry().getServiceName()));
                                    NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudProxyCouldNotStartEvent(taskedService.getEntry().getServiceName()));
                                } else {
                                    Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudServiceCouldNotStartEvent(taskedService.getEntry().getServiceName()));
                                    NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudServiceCouldNotStartEvent(taskedService.getEntry().getServiceName()));
                                }
                            }
                        });
                    }
                }
            }, 0, 10, TimeUtil.SECONDS);


            /*
            Update all Services
             */

            new TimerBase().scheduleAsync(new TimerTask() {
                @Override
                public void run() {
                    if (!PrexorCloudManager.shutdown) {
                        services.stream()
                                .filter(taskedService -> taskedService.getEntry().getServiceState() != ServiceState.STARTED && taskedService.getEntry().getServiceState() != ServiceState.QUEUED)
                                .forEach(taskedService -> NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutServiceConnected(taskedService.getEntry().getServiceName(), taskedService.getEntry().getGroupName())));

                        PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                        general.getPlayers().forEach(s -> {
                            CloudPlayerRestCache restCech = (CloudPlayerRestCache) (PrexorCloudManager.restDriver.convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + s), CloudPlayerRestCache.class));
                            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutPlayerConnect(UUIDDriver.getUsername(UUID.fromString(s)), restCech.getProxy()));
                        });
                    }
                }
            }, 0, 3, TimeUtil.MINUTES);
        });

        current.setPriority(1);
        current.start();
    }

    @Override
    public TaskedService getService(String service) {
        return this.services.stream().noneMatch(service1 -> service1.getEntry().getServiceName().equals(service)) ? null : this.services.stream().filter(service1 -> service1.getEntry().getServiceName().equals(service)).findFirst().orElse(null);
    }

    @Override
    public ArrayDeque<TaskedService> getServices() {
        return this.services;
    }

    @Override
    public List<TaskedService> getServices(String group) {
        return this.services.stream().filter(service1 -> service1.getEntry().getGroupName().equals(group)).collect(Collectors.toList());
    }

    @Override
    public List<TaskedService> getServicesFromNode(String node) {
        return this.services.stream().filter(service1 -> service1.getEntry().getTaskNode().equals(node)).collect(Collectors.toList());
    }
}

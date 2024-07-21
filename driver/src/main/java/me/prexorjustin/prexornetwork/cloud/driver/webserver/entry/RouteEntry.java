package me.prexorjustin.prexornetwork.cloud.driver.webserver.entry;

import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.interfaces.IRouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPICreateEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPIUpdateEvent;

@NoArgsConstructor
public class RouteEntry implements IRouteEntry {
    public String route;
    private String jsonOptions;

    public RouteEntry(String route, String jsonOptions) {
        this.route = route;
        this.jsonOptions = jsonOptions;
        NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudRestAPICreateEvent(this.route, this.jsonOptions));
    }

    @Override
    public String channelRead() {
        return this.jsonOptions;
    }

    @Override
    public void channelWrite(String option) {
        this.jsonOptions = option;
    }

    @Override
    public String readROUTE() {
        return this.route;
    }

    @Override
    public void channelUpdate(String update) {
        this.jsonOptions = update;
        NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudRestAPIUpdateEvent(this.route, update));
    }
}

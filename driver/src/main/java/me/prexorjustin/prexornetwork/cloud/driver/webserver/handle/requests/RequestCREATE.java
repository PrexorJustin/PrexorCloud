package me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.requests;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPICreateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.RequestHandler;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPICreateEvent;

public class RequestCREATE {

    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.contains("/")) {
            if (uri.contains("/setup/")) return;

            String authenticatorKey = uri.split("/")[1];

            if (authenticatorKey.length() > 4 && Driver.getInstance().getWebServer().AUTH_KEY.equals(authenticatorKey)) {
                String path = uri.replace("/" + authenticatorKey, "");

                if (Driver.getInstance().getWebServer().getRoutes(path) != null) {
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                    ctx.writeAndFlush(response);
                } else if (!path.isEmpty()) {
                    ByteBuf content = request.content();
                    String payload = content.toString(CharsetUtil.UTF_8);

                    Driver.getInstance().getWebServer().addRoute(new RouteEntry(path, payload));
                    Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudRestAPICreateEvent(path, payload));
                    NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudRestAPICreateEvent(path, payload));

                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.OK, "{\"reason\":\"data received\"}");
                    ctx.writeAndFlush(response);
                } else {
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                    ctx.writeAndFlush(response);
                }
            } else {
                FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter the right auth-key\"}");
                ctx.writeAndFlush(response);
            }
        } else {
            FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter the right auth-key\"}");
            ctx.writeAndFlush(response);
        }
    }
}

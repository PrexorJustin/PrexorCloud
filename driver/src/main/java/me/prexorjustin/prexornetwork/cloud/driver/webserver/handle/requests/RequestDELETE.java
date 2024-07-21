package me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.requests;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPIDeleteEvent;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.RequestHandler;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPIDeleteEvent;

public class RequestDELETE {
    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.contains("/")) {
            if (uri.contains("/setup/")) return;

            String authenticatorKey = uri.split("/")[1];

            if (authenticatorKey.length() > 4 && (Driver.getInstance().getWebServer()).AUTH_KEY.equals(authenticatorKey)) {
                String path = uri.replace("/" + authenticatorKey, "");

                if (Driver.getInstance().getWebServer().getRoutes(path) == null) {
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"the path not exists\"}");
                    ctx.writeAndFlush(response);
                } else if (!path.isEmpty()) {
                    Driver.getInstance().getWebServer().removeRoute(path);
                    Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudRestAPIDeleteEvent(path));
                    NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudRestAPIDeleteEvent(path));

                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.OK, "{\"reason\":\"data received\"}");
                    ctx.writeAndFlush(response);
                } else {
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"the path your entered is empty\"}");
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

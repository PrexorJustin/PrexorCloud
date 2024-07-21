package me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.requests;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.RequestHandler;

public class RequestGET {

    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();

        if (uri.contains("/") && uri.length() > 7) {
            if (uri.contains("/setup/")) {
                if (Driver.getInstance().getWebServer().getRoutes(uri) == null) {
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                    ctx.writeAndFlush(response);
                } else {
                    String json = Driver.getInstance().getWebServer().getRoute(uri);
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.OK, json);
                    Driver.getInstance().getWebServer().removeRoute(uri);
                    ctx.writeAndFlush(response);
                }
            } else {
                String authenticatorKey = uri.split("/")[1];

                if (authenticatorKey.length() > 4 && Driver.getInstance().getWebServer().AUTH_KEY.equals(authenticatorKey)) {
                    String path = uri.replace("/" + authenticatorKey, "");
                    if (Driver.getInstance().getWebServer().getRoutes(path) == null) {
                        FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                        ctx.writeAndFlush(response);
                    } else if (!path.isEmpty()) {
                        String json = Driver.getInstance().getWebServer().getRoute(path);
                        FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.OK, json);
                        ctx.writeAndFlush(response);
                    } else {
                        FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                        ctx.writeAndFlush(response);
                    }
                } else {
                    FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter the right auth-key\"}");
                    ctx.writeAndFlush(response);
                }
            }

        } else {
            FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter the right auth-key\"}");
            ctx.writeAndFlush(response);
        }
    }
}

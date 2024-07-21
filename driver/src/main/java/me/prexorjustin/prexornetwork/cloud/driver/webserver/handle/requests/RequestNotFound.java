package me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.requests;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.RequestHandler;

public class RequestNotFound {

    public void handle(ChannelHandlerContext ctx) throws Exception {
        FullHttpResponse response = RequestHandler.createResponse(HttpResponseStatus.METHOD_NOT_ALLOWED, "{\"reason\":\"Failed, because no HttpRequest was found\"}");
        ctx.writeAndFlush(response);
    }

}

package me.prexorjustin.prexornetwork.cloud.driver.webserver.handle;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.requests.*;

import static io.netty.handler.codec.http.HttpMethod.*;

public class RequestHandler extends ChannelInboundHandlerAdapter {

    public static FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext handlerContext, Object message) throws Exception {
        if (message instanceof FullHttpRequest request) {
            HttpMethod method = request.method();

            if (method != null) {
                if (GET.equals(method)) {
                    new RequestGET().handle(handlerContext, request);
                } else if (PUT.equals(method)) {
                    new RequestUPDATE().handle(handlerContext, request);
                } else if (POST.equals(method)) {
                    new RequestCREATE().handle(handlerContext, request);
                } else if (DELETE.equals(method)) {
                    new RequestDELETE().handle(handlerContext, request);
                } else {
                    new RequestNotFound().handle(handlerContext);
                }
            } else {
                new RequestNotFound().handle(handlerContext);
            }
        }
    }
}

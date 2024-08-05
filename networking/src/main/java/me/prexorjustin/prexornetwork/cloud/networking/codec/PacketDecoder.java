package me.prexorjustin.prexornetwork.cloud.networking.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @SneakyThrows
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() < 4) return;

        int packetUUID = byteBuf.readInt();
        var packetClass = NettyDriver.getInstance().getPacketDriver().getPacket(packetUUID);
        if (packetClass != null) {
            final var packet = packetClass.getDeclaredConstructor().newInstance();
            packet.readPacket(new NettyBuffer(byteBuf));
            list.add(packet);
        }
    }
}

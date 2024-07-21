package me.prexorjustin.prexornetwork.cloud.networking.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        NettyBuffer nettyBuffer = new NettyBuffer(byteBuf);
        int packetUUID = packet.getPacketUUID();
        byteBuf.writeInt(packetUUID);
        packet.writePacket(nettyBuffer);
    }
}

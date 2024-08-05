package me.prexorjustin.prexornetwork.cloud.networking.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class Packet {

    private int packetUUID;

    public abstract void readPacket(NettyBuffer buffer);

    public abstract void writePacket(NettyBuffer buffer);
}

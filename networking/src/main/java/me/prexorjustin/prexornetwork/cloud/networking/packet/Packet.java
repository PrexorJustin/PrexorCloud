package me.prexorjustin.prexornetwork.cloud.networking.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Getter
@Setter
public abstract class Packet {

    private int packetUUID;

    public abstract void readPacket(@NotNull NettyBuffer buffer);

    public abstract void writePacket(@NotNull NettyBuffer buffer);
}

package me.prexorjustin.prexornetwork.cloud.networking.packet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class NettyBuffer {

    protected static final Gson GSON = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    private final ByteBuf byteBuf;

    public void writeString(String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        byteBuf.ensureWritable(8 + bytes.length);

        try {
            byteBuf.writeLong(bytes.length);
            byteBuf.writeBytes(bytes);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public String readString() {
        if (byteBuf.readableBytes() < 8)
            throw new IllegalStateException("Not enough data to read Message (byteBuf.readableBytes() < 8): " + byteBuf.readableBytes());

        long messageLength = byteBuf.readLong();
        if (byteBuf.readableBytes() < messageLength)
            throw new IllegalStateException("Not enough data to read Message (byteBuf.readableBytes() < messageLength): " + byteBuf.readableBytes());

        byte[] bytes = new byte[(int) messageLength];
        byteBuf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeInt(int integer) {
        byteBuf.writeInt(integer);
    }

    public int readInt() {
        return byteBuf.readInt();
    }

    public void writeClass(Object o) {
        writeString(GSON.toJson(o));
    }

    @SneakyThrows
    public Object readClass(Class<?> c) {
        String read = readString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(read, c);
    }

    public void writeBoolean(boolean bool) {
        byteBuf.writeBoolean(bool);
    }

    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }
}

package ru.enke.sansara.network.handler;

import org.jetbrains.annotations.Nullable;
import ru.enke.minecraft.protocol.packet.PacketMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlerRegistry {

    private final Map<Class<? extends PacketMessage>, MessageHandler> handlers = new HashMap<>();

    public void registerHandler(final Class<? extends PacketMessage> cls, final MessageHandler handler) {
        handlers.put(cls, handler);
    }

    @Nullable
    public MessageHandler getMessageHandler(final PacketMessage msg) {
        return handlers.get(msg.getClass());
    }

}

package me.wesley1808.advancedchat.impl.data;

import com.google.gson.*;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;

import java.lang.reflect.Type;

public class Json {
    public static final Gson PLAYER_DATA = new GsonBuilder()
            .registerTypeHierarchyAdapter(ChatChannel.class, new ChannelSerializer())
            .disableHtmlEscaping()
            .setLenient()
            .create();

    public static final Gson CONFIG = new GsonBuilder()
            .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.INSTANCE)
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static class ChannelSerializer implements JsonSerializer<ChatChannel>, JsonDeserializer<ChatChannel> {

        @Override
        public ChatChannel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return Channels.get(json.getAsString());
            }
            return null;
        }

        @Override
        public JsonElement serialize(ChatChannel channel, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(channel.name);
        }
    }
}

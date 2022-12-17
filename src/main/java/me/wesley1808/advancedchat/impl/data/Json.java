package me.wesley1808.advancedchat.impl.data;

import com.google.gson.*;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class Json {
    public static final Gson PLAYER_DATA = new GsonBuilder()
            .registerTypeHierarchyAdapter(ChatChannel.class, new ChannelSerializer())
            .disableHtmlEscaping()
            .setLenient()
            .create();

    public static final Gson CONFIG = new GsonBuilder()
            .registerTypeHierarchyAdapter(Pattern.class, new PatternSerializer())
            .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.INSTANCE)
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(BuiltInRegistries.SOUND_EVENT))
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static class PatternSerializer implements JsonSerializer<Pattern>, JsonDeserializer<Pattern> {

        @Override
        public Pattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return Pattern.compile(json.getAsString(), Pattern.CASE_INSENSITIVE);
            }
            return null;
        }

        @Override
        public JsonElement serialize(Pattern pattern, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(pattern.pattern());
        }
    }

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

    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {

        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                ResourceLocation location = ResourceLocation.tryParse(json.getAsString());
                if (location != null) {
                    return this.registry.get(location);
                }
            }
            return null;
        }

        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            ResourceLocation location = this.registry.getKey(src);
            return new JsonPrimitive(location != null ? location.toString() : "");
        }
    }
}

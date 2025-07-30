package mekanism.api.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class MekanismStreamCodecs {
    public static <B extends FriendlyByteBuf, V extends Enum<V>> StreamCodec<B, V> enumCodec(Class<V> enumClass) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buf) {
                return buf.readEnum(enumClass);
            }

            @Override
            public void encode(B buf, V value) {
                buf.writeEnum(value);
            }
        };
    }
}

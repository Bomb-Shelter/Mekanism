package mekanism.common.inventory.container.type;

import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.network.to_client.qio.BulkQIOData;
import mekanism.common.inventory.container.entity.IEntityContainer;
import mekanism.common.inventory.container.tile.QIODashboardContainer;
import mekanism.common.inventory.container.type.MekanismContainerType.IMekanismContainerFactory;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.qio.TileEntityQIODashboard;
import mekanism.common.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MekanismContainerType<T, CONTAINER extends AbstractContainerMenu> extends BaseMekanismContainerType<T, CONTAINER, IMekanismContainerFactory<T, CONTAINER>> {

    public static <TILE extends TileEntityMekanism, CONTAINER extends AbstractContainerMenu> MekanismContainerType<TILE, CONTAINER> tile(Class<TILE> type,
          IMekanismContainerFactory<TILE, CONTAINER> constructor) {
        return new MekanismContainerType<>(type, constructor, constructor, tileCodec(type));
    }

    public static <TILE extends TileEntityMekanism, CONTAINER extends AbstractContainerMenu> MekanismContainerType<TILE, CONTAINER> tile(Class<TILE> type,
          IMekanismSidedContainerFactory<TILE, CONTAINER> constructor) {
        return new MekanismContainerType<>(type, constructor, (id, inv, tile) -> constructor.create(id, inv, tile, true), tileCodec(type));
    }

    public record QIOData(TileEntityQIODashboard tile, BulkQIOData bulkQIO) {}

    public static StreamCodec<RegistryFriendlyByteBuf, QIOData> qioCodec(@Nullable QIOFrequency frequency) {
        return StreamCodec.composite(
                tileCodec(TileEntityQIODashboard.class),
                QIOData::tile,
                BulkQIOData.streamCodec(frequency),
                QIOData::bulkQIO,
                QIOData::new
        );
    }

    public static  MekanismContainerType<QIOData, QIODashboardContainer> qioDashboard() {
        return new MekanismContainerType<>(QIOData.class,
              (id, inv, data) -> new QIODashboardContainer(id, inv, data.tile(), false, BulkQIOData.INITIAL_SERVER),
              (id, inv, data) -> new QIODashboardContainer(id, inv, data.tile(), true, data.bulkQIO()),
              qioCodec(null)
        );
    }

    public static <ENTITY extends Entity, CONTAINER extends AbstractContainerMenu & IEntityContainer<ENTITY>> MekanismContainerType<ENTITY, CONTAINER> entity(Class<ENTITY> type,
          IMekanismContainerFactory<ENTITY, CONTAINER> constructor) {
        return new MekanismContainerType<>(type, constructor, constructor, entityCodec(type));
    }

    public static <ENTITY extends Entity, CONTAINER extends AbstractContainerMenu & IEntityContainer<ENTITY>> MekanismContainerType<ENTITY, CONTAINER> entity(Class<ENTITY> type,
          IMekanismSidedContainerFactory<ENTITY, CONTAINER> constructor) {
        return new MekanismContainerType<>(type, constructor, (id, inv, e) -> constructor.create(id, inv, e, true), entityCodec(type));
    }

    protected MekanismContainerType(Class<T> type, IMekanismContainerFactory<T, CONTAINER> mekanismConstructor, ExtendedFactory<CONTAINER, T> constructor, StreamCodec<? super RegistryFriendlyByteBuf, T> packetCodec) {
        super(type, mekanismConstructor, constructor, packetCodec);
    }

    @Nullable
    public CONTAINER create(int id, Inventory inv, Object data) {
        if (type.isInstance(data)) {
            return mekanismConstructor.create(id, inv, type.cast(data));
        }
        return null;
    }

    @Nullable
    public MenuConstructor create(Object data) {
        if (type.isInstance(data)) {
            T d = type.cast(data);
            return (id, inv, player) -> mekanismConstructor.create(id, inv, d);
        }
        return null;
    }

    public static <TILE extends BlockEntity> StreamCodec<FriendlyByteBuf, TILE> tileCodec(Class<TILE> type) {
        return StreamCodec.ofMember((tile, buf) -> buf.writeBlockPos(tile.getBlockPos()), buf -> getTileFromBuf(buf, type));
    }

    @NotNull
    private static <TILE extends BlockEntity> TILE getTileFromBuf(FriendlyByteBuf buf, Class<TILE> type) {
        if (buf == null) {
            throw new IllegalArgumentException("Null packet buffer");
        } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new UnsupportedOperationException("This method is only supported on the client.");
        }
        BlockPos pos = buf.readBlockPos();
        TILE tile = WorldUtils.getTileEntity(type, Minecraft.getInstance().level, pos);
        if (tile == null) {
            throw new IllegalStateException("Client could not locate tile at " + pos + " for tile container. "
                                            + "This is likely caused by a mod breaking client side tile lookup");
        }
        return tile;
    }

    public static <ENTITY extends Entity> StreamCodec<RegistryFriendlyByteBuf, ENTITY> entityCodec(Class<ENTITY> type) {
        return StreamCodec.ofMember((e, buf) -> buf.writeVarInt(e.getId()), buf -> getEntityFromBuf(buf, type));
    }

    @NotNull
    private static <ENTITY extends Entity> ENTITY getEntityFromBuf(FriendlyByteBuf buf, Class<ENTITY> type) {
        if (buf == null) {
            throw new IllegalArgumentException("Null packet buffer");
        } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new UnsupportedOperationException("This method is only supported on the client.");
        }
        if (Minecraft.getInstance().level == null) {
            throw new IllegalStateException("Client world is null.");
        }
        int entityId = buf.readVarInt();
        Entity e = Minecraft.getInstance().level.getEntity(entityId);
        if (type.isInstance(e)) {
            //noinspection unchecked
            return (ENTITY) e;
        }
        throw new IllegalStateException("Client could not locate entity (id: " + entityId + ")  for entity container or the entity was of an invalid type. "
                                        + "This is likely caused by a mod breaking client side entity lookup.");
    }

    @FunctionalInterface
    public interface IMekanismContainerFactory<T, CONTAINER extends AbstractContainerMenu> extends ExtendedFactory<CONTAINER, T> {

        CONTAINER create(int id, Inventory inv, T data);
    }

    @FunctionalInterface
    public interface IMekanismSidedContainerFactory<T, CONTAINER extends AbstractContainerMenu> extends IMekanismContainerFactory<T, CONTAINER> {


        CONTAINER create(int id, Inventory inv, T data, boolean remote);

        @Override
        default CONTAINER create(int id, Inventory inv, T data) {
            return create(id, inv, data, false);
        }
    }
}
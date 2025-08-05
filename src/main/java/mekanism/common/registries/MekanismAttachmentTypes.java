package mekanism.common.registries;

import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.Mekanism;
import mekanism.common.item.gear.ItemFlamethrower.FlamethrowerMode;
import mekanism.common.lib.radiation.MeltdownLevelData;
import mekanism.common.lib.radiation.RadiationLevelData;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@NothingNullByDefault
public class MekanismAttachmentTypes {

    private MekanismAttachmentTypes() {
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Mekanism.MODID);

    //Note: We do not specify copy on death as we want radiation to reset to baseline on death
    public static final AttachmentType<Double> RADIATION = AttachmentRegistry.create(Mekanism.rl("radiation"),
                    builder -> builder.initializer(IRadiationManager.INSTANCE::baselineRadiation)
                .persistent(Codec.doubleRange(IRadiationManager.INSTANCE.baselineRadiation(), Double.MAX_VALUE), radiation -> radiation > IRadiationManager.INSTANCE.baselineRadiation())
                .copyHandler((radiation, holder, provider) -> radiation > IRadiationManager.INSTANCE.baselineRadiation() ? radiation : null)
                .build()
    );

    public static final AttachmentType<FlamethrowerMode> FLAMETHROWER_MODE = ATTACHMENT_TYPES.register("flamethrower_mode", () ->
          AttachmentType.builder(() -> FlamethrowerMode.COMBAT)
                .serialize(FlamethrowerMode.CODEC, mode -> mode != FlamethrowerMode.COMBAT)
                .build());

    public static final AttachmentType<MeltdownLevelData> MELTDOWN_DATA = ATTACHMENT_TYPES.register("meltdown_data", () -> AttachmentType.serializable(MeltdownLevelData::new).build());

    public static final AttachmentType<RadiationLevelData> RADIATION_LEVEL_DATA = ATTACHMENT_TYPES.register("radiation_data", () -> AttachmentType.serializable(RadiationLevelData::new).build());
}
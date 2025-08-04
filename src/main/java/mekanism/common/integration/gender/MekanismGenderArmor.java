package mekanism.common.integration.gender;

import com.wildfire.api.IGenderArmor;
import mekanism.common.Mekanism;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.world.level.ItemLike;

public record MekanismGenderArmor(boolean coversBreasts, boolean alwaysHidesBreasts, float physicsResistance, float tightness, boolean armorStandsCopySettings) implements IGenderArmor {

    private static final ItemApiLookup<IGenderArmor, Void> GENDER_ARMOR_CAPABILITY = ItemApiLookup.get(Mekanism.hooks.genderMod.rl("gender_armor"), IGenderArmor.class, void.class);
    public static final MekanismGenderArmor OPEN_FRONT = new MekanismGenderArmor(false, false, 0, 0, false);
    public static final MekanismGenderArmor HIDES_BREASTS = new MekanismGenderArmor(true, true, 0, 0, false);
    public static final MekanismGenderArmor HAZMAT = new MekanismGenderArmor(0.5F, 0.25F, false);

    public MekanismGenderArmor(float physicsResistance) {
        this(physicsResistance, 0);
    }

    public MekanismGenderArmor(float physicsResistance, float tightness) {
        this(physicsResistance, tightness, true);
    }

    public MekanismGenderArmor(float physicsResistance, float tightness, boolean armorStandsCopySettings) {
        this(true, false, physicsResistance, tightness, armorStandsCopySettings);
    }

    public MekanismGenderArmor {
        if (physicsResistance < 0 || physicsResistance > 1) {
            throw new IllegalArgumentException("Physics resistance must be between zero and one inclusive.");
        } else if (tightness < 0 || tightness > 1) {
            throw new IllegalArgumentException("Armor tightness must be between zero and one inclusive.");
        }
    }

    public void register(ItemLike... items) {
        GENDER_ARMOR_CAPABILITY.registerForItems((stack, ctx) -> this, items);
    }
}
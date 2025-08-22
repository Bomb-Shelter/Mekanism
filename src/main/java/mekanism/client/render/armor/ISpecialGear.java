package mekanism.client.render.armor;

import org.jetbrains.annotations.NotNull;

public interface ISpecialGear extends IClientItemExtensions {

    @NotNull
    ICustomArmor gearModel();
}
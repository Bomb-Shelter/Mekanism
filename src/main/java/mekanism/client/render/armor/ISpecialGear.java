package mekanism.client.render.armor;

import io.github.fabricators_of_create.porting_lib.client_extensions.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public interface ISpecialGear extends IClientItemExtensions {

    @NotNull
    ICustomArmor gearModel();
}
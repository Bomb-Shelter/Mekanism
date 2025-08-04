package mekanism.common.config.value;

import mekanism.common.config.IMekanismConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigSpec.EnumValue;
import io.github.fabricators_of_create.porting_lib.core.util.TranslatableEnum;

public class CachedEnumValue<T extends Enum<T>> extends CachedConfigValue<T> {

    private CachedEnumValue(IMekanismConfig config, EnumValue<T> internal) {
        super(config, internal);
    }

    //Note: Ensure that we provide a nice translated name for any enum value based configs we have
    public static <T extends Enum<T> & TranslatableEnum> CachedEnumValue<T> wrap(IMekanismConfig config, EnumValue<T> internal) {
        return new CachedEnumValue<>(config, internal);
    }
}
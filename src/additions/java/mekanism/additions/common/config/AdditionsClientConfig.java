package mekanism.additions.common.config;

import mekanism.common.config.BaseMekanismConfig;
import mekanism.common.config.value.CachedBooleanValue;
import io.github.fabricators_of_create.porting_lib.config.ModConfig.Type;
import io.github.fabricators_of_create.porting_lib.config.ModConfigSpec;

public class AdditionsClientConfig extends BaseMekanismConfig {

    private final ModConfigSpec configSpec;

    public final CachedBooleanValue pushToTalk;

    AdditionsClientConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        pushToTalk = CachedBooleanValue.wrap(this, AdditionsConfigTranslations.CLIENT_PUSH_TO_TALK.applyToBuilder(builder)
              .define("pushToTalk", true));

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "additions-client";
    }

    @Override
    public String getTranslation() {
        return "Client Config";
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.CLIENT;
    }
}
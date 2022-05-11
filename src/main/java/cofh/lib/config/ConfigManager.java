package cofh.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    protected List<IBaseConfig> serverSubConfigs = new ArrayList<>();
    protected List<IBaseConfig> clientSubConfigs = new ArrayList<>();

    protected boolean clientInit = false;
    protected boolean serverInit = false;

    protected final ForgeConfigSpec.Builder serverConfig = new ForgeConfigSpec.Builder();
    protected ForgeConfigSpec serverSpec;

    protected final ForgeConfigSpec.Builder clientConfig = new ForgeConfigSpec.Builder();
    protected ForgeConfigSpec clientSpec;

    public ConfigManager register(IEventBus bus) {

        bus.register(this);
        return this;
    }

    public ConfigManager addClientConfig(IBaseConfig config) {

        clientSubConfigs.add(config);
        return this;
    }

    public ConfigManager addServerConfig(IBaseConfig config) {

        serverSubConfigs.add(config);
        return this;
    }

    /**
     * Must be called in Mod Constructor or NewRegistryEvent at latest.
     */
    public void setupClient() {

        if (!clientInit) {
            genClientConfig();
            clientSpec = clientConfig.build();
            refreshClientConfig();
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
            clientInit = true;
        }
    }

    /**
     * Must be called in Mod Constructor or Common Setup event at latest.
     */
    public void setupServer() {

        if (!serverInit) {
            genServerConfig();
            serverSpec = serverConfig.build();
            refreshServerConfig();
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
            serverInit = true;
        }
    }

    public ForgeConfigSpec getServerSpec() {

        return serverSpec;
    }

    public ForgeConfigSpec getClientSpec() {

        return clientSpec;
    }

    protected void genServerConfig() {

        for (IBaseConfig cfg : serverSubConfigs) {
            cfg.apply(serverConfig);
        }
    }

    protected void genClientConfig() {

        for (IBaseConfig cfg : clientSubConfigs) {
            cfg.apply(clientConfig);
        }
    }

    protected void refreshServerConfig() {

        serverSubConfigs.forEach(IBaseConfig::refresh);
    }

    protected void refreshClientConfig() {

        clientSubConfigs.forEach(IBaseConfig::refresh);
    }

    // region CONFIGURATION
    @SubscribeEvent
    public void configLoading(ModConfigEvent.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public void configReloading(ModConfigEvent.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
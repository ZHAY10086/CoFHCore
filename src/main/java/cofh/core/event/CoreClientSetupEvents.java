package cofh.core.event;

import cofh.core.client.particle.*;
import cofh.lib.api.item.IColorableItem;
import cofh.lib.client.model.DynamicFluidContainerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.init.CoreParticles.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreClientSetupEvents {

    private static final List<Item> COLORABLE_ITEMS = new ArrayList<>();

    private CoreClientSetupEvents() {

    }

    @SubscribeEvent
    public static void colorSetupItem(final ColorHandlerEvent.Item event) {

        ItemColors colors = event.getItemColors();
        for (Item colorable : COLORABLE_ITEMS) {
            colors.register(((IColorableItem) colorable)::getColor, colorable);
        }
    }

    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent event) {

        ModelLoaderRegistry.registerLoader(new ResourceLocation(ID_COFH_CORE, "dynamic_fluid"), new DynamicFluidContainerModel.Loader());
    }

    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {

        ParticleEngine manager = Minecraft.getInstance().particleEngine;

        manager.register(FROST.get(), FrostParticle.Factory::new);
        manager.register(SPARK.get(), SparkParticle.Factory::new);
        manager.register(PLASMA.get(), PlasmaBallParticle.Factory::new);
        manager.register(SHOCKWAVE.get(), ShockwaveParticle.Factory::new);
        manager.register(BLAST_WAVE.get(), BlastWaveParticle.Factory::new);
        manager.register(VORTEX.get(), WindVortexParticle.Factory::new);
        manager.register(SPIRAL.get(), WindSpiralParticle.Factory::new);
        manager.register(STRAIGHT_ARC.get(), ArcParticle.Factory::new);
        manager.register(MIST.get(), MistParticle::iceMist);

    }

    // region HELPERS
    public static void addColorable(Item colorable) {

        if (colorable instanceof IColorableItem) {
            COLORABLE_ITEMS.add(colorable);
        }
    }
    // endregion
}

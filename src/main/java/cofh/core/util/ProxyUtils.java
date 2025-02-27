package cofh.core.util;

import cofh.core.CoFHCore;
import cofh.lib.api.IProxyItemPropertyGetter;
import cofh.lib.api.block.entity.IAreaEffectTile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ProxyUtils {

    private ProxyUtils() {

    }

    public static void addIndexedChatMessage(Component chat, int index) {

        CoFHCore.PROXY.addIndexedChatMessage(chat, index);
    }

    public static void playSimpleSound(SoundEvent sound, float volume, float pitch) {

        CoFHCore.PROXY.playSimpleSound(sound, volume, pitch);
    }

    public static Player getClientPlayer() {

        return CoFHCore.PROXY.getClientPlayer();
    }

    public static Level getClientWorld() {

        return CoFHCore.PROXY.getClientWorld();
    }

    public static boolean isClient() {

        return CoFHCore.PROXY.isClient();
    }

    public static boolean canLocalize(String key) {

        return CoFHCore.PROXY.canLocalize(key);
    }

    public static synchronized Object addModel(Item item, Object model) {

        return CoFHCore.PROXY.addModel(item, model);
    }

    public static Object getModel(ResourceLocation loc) {

        return CoFHCore.PROXY.getModel(loc);
    }

    public static void registerColorable(Item colorable) {

        CoFHCore.PROXY.addColorable(colorable);
    }

    public static void registerItemModelProperty(Item item, ResourceLocation resourceLoc, IProxyItemPropertyGetter propertyGetter) {

        CoFHCore.PROXY.registerItemModelProperty(item, resourceLoc, propertyGetter);
    }

    public static void addAreaEffectTile(IAreaEffectTile tile) {

        CoFHCore.PROXY.addAreaEffectTile(tile);
    }

    public static void removeAreaEffectTile(IAreaEffectTile tile) {

        CoFHCore.PROXY.removeAreaEffectTile(tile);
    }

}

package cofh.lib.api.item;

import cofh.lib.util.Utils;
import net.minecraft.world.item.ItemStack;

import static cofh.core.util.references.CoreReferences.HOLDING;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;

/**
 * Marker interface for anything that supports the "Holding" enchantment. Can also be done via the Enchantable capability, but this is way less overhead.
 */
public interface IContainerItem extends ICoFHItem {

    default int getMaxStored(ItemStack container, int amount) {

        int holding = getItemEnchantmentLevel(HOLDING, container);
        return Utils.getEnchantedCapacity(amount, holding);
    }

}

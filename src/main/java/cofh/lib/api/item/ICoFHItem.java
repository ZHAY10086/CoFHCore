package cofh.lib.api.item;

import cofh.core.item.IMultiModeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static cofh.core.client.CoreKeys.MULTIMODE_DECREMENT;
import static cofh.core.client.CoreKeys.MULTIMODE_INCREMENT;
import static cofh.lib.util.Utils.getKeynameFromKeycode;
import static cofh.lib.util.constants.NBTTags.TAG_ACTIVE;
import static cofh.lib.util.helpers.StringHelper.*;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;

/**
 * Hacky default interface to reduce boilerplate. :)
 */
public interface ICoFHItem extends IForgeItem {

    ICoFHItem setDisplayGroup(Supplier<CreativeModeTab> displayGroup);

    ICoFHItem setModId(String modId);

    ICoFHItem setShowInGroups(Supplier<Boolean> showInGroups);

    default void addEnergyTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, int extract, int receive, boolean creative) {

        if (extract == receive && extract > 0 || creative) {
            tooltip.add(getTextComponent(localize("info.cofh.transfer") + ": " + getScaledNumber(extract) + " RF/t"));
        } else if (extract > 0) {
            if (receive > 0) {
                tooltip.add(getTextComponent(localize("info.cofh.send") + "|" + localize("info.cofh.receive") + ": " + getScaledNumber(extract) + "|" + getScaledNumber(receive) + " RF/t"));
            } else {
                tooltip.add(getTextComponent(localize("info.cofh.send") + ": " + getScaledNumber(extract) + " RF/t"));
            }
        } else if (receive > 0) {
            tooltip.add(getTextComponent(localize("info.cofh.receive") + ": " + getScaledNumber(receive) + " RF/t"));
        }
    }

    default void addModeChangeTooltip(IMultiModeItem item, ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        if (item.getNumModes(stack) <= 2) {
            addIncrementModeChangeTooltip(item, stack, worldIn, tooltip, flagIn);
            return;
        }
        tooltip.add(new TranslatableComponent("info.cofh.mode_change", getKeynameFromKeycode(MULTIMODE_INCREMENT.getKey().getValue()), getKeynameFromKeycode(MULTIMODE_DECREMENT.getKey().getValue())).withStyle(YELLOW));

        if (MULTIMODE_INCREMENT.getKey().getValue() == -1) {
            tooltip.add(new TranslatableComponent("info.cofh.key_not_bound", localize("key.cofh.mode_change_increment")).withStyle(RED));
        }
        if (MULTIMODE_DECREMENT.getKey().getValue() == -1) {
            tooltip.add(new TranslatableComponent("info.cofh.key_not_bound", localize("key.cofh.mode_change_decrement")).withStyle(RED));
        }
    }

    default void addIncrementModeChangeTooltip(IMultiModeItem item, ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent("info.cofh.mode_toggle", getKeynameFromKeycode(MULTIMODE_INCREMENT.getKey().getValue())).withStyle(YELLOW));

        if (MULTIMODE_INCREMENT.getKey().getValue() == -1) {
            tooltip.add(new TranslatableComponent("info.cofh.key_not_bound", localize("key.cofh.mode_change_increment")).withStyle(RED));
        }
    }

    default boolean isActive(ItemStack stack) {

        return stack.getOrCreateTag().getBoolean(TAG_ACTIVE);
    }

    default void setActive(ItemStack stack, boolean state) {

        stack.getOrCreateTag().putBoolean(TAG_ACTIVE, state);
    }

    default boolean hasActiveTag(ItemStack stack) {

        return stack.getOrCreateTag().contains(TAG_ACTIVE);
    }

    default void setActive(ItemStack stack, LivingEntity entity) {

        stack.getOrCreateTag().putLong(TAG_ACTIVE, entity.level.getGameTime() + 20);
    }

}

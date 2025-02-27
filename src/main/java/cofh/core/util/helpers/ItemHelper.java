package cofh.core.util.helpers;

import cofh.core.item.ILeftClickHandlerItem;
import cofh.core.item.IMultiModeItem;
import com.google.common.base.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemHelper {

    private ItemHelper() {

    }

    public static ItemStack consumeItem(ItemStack stack, int amount) {

        if (amount <= 0) {
            return stack;
        }
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = stack.getItem();
        boolean largerStack = stack.getItem().getItemStackLimit(stack) > 1;
        // vanilla only alters the stack passed to hasContainerItem/etc. when the size is > 1

        if (largerStack) {
            stack.shrink(amount);
            if (stack.isEmpty()) {
                stack = ItemStack.EMPTY;
            }
        } else if (item.hasContainerItem(stack)) {
            ItemStack ret = item.getContainerItem(stack);
            if (ret.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (ret.isDamageableItem() && ret.getDamageValue() > ret.getMaxDamage()) {
                ret = ItemStack.EMPTY;
            }
            return ret;
        }
        return largerStack ? stack : ItemStack.EMPTY;
    }

    // region CLONESTACK
    public static ItemStack cloneStack(Item item) {

        return cloneStack(item, 1);
    }

    public static ItemStack cloneStack(Block block) {

        return cloneStack(block, 1);
    }

    public static ItemStack cloneStack(Item item, int stackSize) {

        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, stackSize);
    }

    public static ItemStack cloneStack(Block block, int stackSize) {

        if (block == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(block, stackSize);
    }

    public static ItemStack cloneStack(ItemStack stack, int stackSize) {

        if (stack.isEmpty() || stackSize <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack retStack = stack.copy();
        retStack.setCount(stackSize);

        return retStack;
    }

    public static ItemStack cloneStack(ItemStack stack) {

        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }
    // endregion

    // region NBT TAGS
    public static ItemStack copyTag(ItemStack container, ItemStack other) {

        if (!other.isEmpty() && other.hasTag()) {
            container.setTag(other.getTag().copy());
        }
        return container;
    }

    public static CompoundTag setItemStackTagName(CompoundTag tag, String name) {

        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        if (tag == null) {
            tag = new CompoundTag();
        }
        if (!tag.contains("display")) {
            tag.put("display", new CompoundTag());
        }
        tag.getCompound("display").putString("Name", name);
        return tag;
    }
    // endregion

    // region COMPARISON
    public static boolean itemsEqualWithTags(ItemStack stackA, ItemStack stackB) {

        return itemsEqual(stackA, stackB) && ItemStack.tagMatches(stackA, stackB);
    }

    public static boolean itemsEqual(ItemStack stackA, ItemStack stackB) {

        return ItemStack.isSame(stackA, stackB);
    }

    /**
     * Compares item, meta, size and nbt of two stacks while ignoring nbt tag keys provided.
     * This is useful in shouldCauseReequipAnimation overrides.
     *
     * @param stackA          first stack to compare
     * @param stackB          second stack to compare
     * @param nbtTagsToIgnore tag keys to ignore when comparing the stacks
     */
    public static boolean areItemStacksEqualIgnoreTags(ItemStack stackA, ItemStack stackB, String... nbtTagsToIgnore) {

        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        }
        if (stackA.isEmpty() || stackB.isEmpty()) {
            return false;
        }
        if (stackA.getItem() != stackB.getItem()) {
            return false;
        }
        if (stackA.getDamageValue() != stackB.getDamageValue()) {
            return false;
        }
        if (stackA.getCount() != stackB.getCount()) {
            return false;
        }
        if (stackA.getTag() == null && stackB.getTag() == null) {
            return true;
        }
        if (stackA.getTag() == null || stackB.getTag() == null) {
            return false;
        }
        int numberOfKeys = stackA.getTag().getAllKeys().size();
        if (numberOfKeys != stackB.getTag().getAllKeys().size()) {
            return false;
        }

        CompoundTag tagA = stackA.getTag();
        CompoundTag tagB = stackB.getTag();

        String[] keys = new String[numberOfKeys];
        keys = tagA.getAllKeys().toArray(keys);

        a:
        for (int i = 0; i < numberOfKeys; ++i) {
            for (int j = 0; j < nbtTagsToIgnore.length; ++j) {
                if (nbtTagsToIgnore[j].equals(keys[i])) {
                    continue a;
                }
            }
            if (!tagA.getCompound(keys[i]).equals(tagB.getCompound(keys[i]))) {
                return false;
            }
        }
        return true;
    }
    // endregion

    // region HELD ITEMS
    public static boolean isPlayerHoldingSomething(Player player) {

        return !getHeldStack(player).isEmpty();
    }

    public static ItemStack getMainhandStack(Player player) {

        return player.getMainHandItem();
    }

    public static ItemStack getOffhandStack(Player player) {

        return player.getOffhandItem();
    }

    public static ItemStack getHeldStack(Player player) {

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            stack = player.getOffhandItem();
        }
        return stack;
    }
    // endregion

    // region MODE CHANGE
    public static ItemStack getHeldMultiModeStack(Player player) {

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof IMultiModeItem)) {
            stack = player.getOffhandItem();
        }
        return stack;
    }

    public static boolean isPlayerHoldingMultiModeItem(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof IMultiModeItem) {
            return true;
        } else {
            heldItem = player.getOffhandItem();
            return heldItem.getItem() instanceof IMultiModeItem;
        }
    }

    public static boolean incrHeldMultiModeItemState(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return mainHand.getItem() instanceof IMultiModeItem
                ? ((IMultiModeItem) mainHand.getItem()).incrMode(mainHand)
                : offHand.getItem() instanceof IMultiModeItem && ((IMultiModeItem) offHand.getItem()).incrMode(offHand);
    }

    public static boolean decrHeldMultiModeItemState(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return mainHand.getItem() instanceof IMultiModeItem
                ? ((IMultiModeItem) mainHand.getItem()).decrMode(mainHand)
                : offHand.getItem() instanceof IMultiModeItem && ((IMultiModeItem) offHand.getItem()).decrMode(offHand);
    }

    public static void onHeldMultiModeItemChange(Player player) {

        ItemStack heldItem = getHeldMultiModeStack(player);
        ((IMultiModeItem) heldItem.getItem()).onModeChange(player, heldItem);
    }
    // endregion

    // region LEFT CLICK
    public static boolean isPlayerHoldingLeftClickItem(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        return player.getMainHandItem().getItem() instanceof ILeftClickHandlerItem;
    }

    public static void onHeldLeftClickItem(Player player) {

        ItemStack heldItem = player.getMainHandItem();
        ((ILeftClickHandlerItem) heldItem.getItem()).onLeftClick(player, heldItem);
    }
    // endregion
}

package cofh.core.util.helpers;

import cofh.lib.inventory.ItemStorageCoFH;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import java.util.List;

import static cofh.core.util.helpers.ItemHelper.itemsEqualWithTags;

/**
 * This class contains helper functions related to Inventories and Inventory manipulation.
 *
 * @author King Lemming
 */
public class InventoryHelper {

    private InventoryHelper() {

    }

    /**
     * Add an ItemStack to an inventory. Return true if the entire stack was added.
     *
     * @param inventory  The inventory.
     * @param stack      ItemStack to add.
     * @param startIndex First slot to attempt to add into. Does not loop around fully.
     * @param endIndex   Final slot to attempt to add into. Should be at most length - 1
     */
    public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex, int endIndex) {

        if (stack.isEmpty()) {
            return true;
        }
        int openSlot = -1;
        for (int i = startIndex; i <= endIndex; ++i) {
            if (itemsEqualWithTags(stack, inventory[i]) && inventory[i].getMaxStackSize() > inventory[i].getCount()) {
                int hold = inventory[i].getMaxStackSize() - inventory[i].getCount();
                if (hold >= stack.getCount()) {
                    inventory[i].grow(stack.getCount());
                    return true;
                } else {
                    stack.shrink(hold);
                    inventory[i].grow(hold);
                }
            } else if (inventory[i].isEmpty() && openSlot == -1) {
                openSlot = i;
            }
        }
        if (openSlot > -1) {
            inventory[openSlot] = stack;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Shortcut method for above, assumes ending slot is length - 1
     */
    public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex) {

        return addItemStackToInventory(inventory, stack, startIndex, inventory.length - 1);
    }

    /**
     * Shortcut method for above, assumes starting slot is 0.
     */
    public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack) {

        return addItemStackToInventory(inventory, stack, 0);
    }

    public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate) {

        return insertStackIntoInventory(handler, stack, simulate, false);
    }

    public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate, boolean forceEmptySlot) {

        return forceEmptySlot ? ItemHandlerHelper.insertItem(handler, stack, simulate) : ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
    }

    public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {

        boolean successful = false;
        int i = reverseDirection ? endIndex - 1 : startIndex;
        int iterOrder = reverseDirection ? -1 : 1;

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }
                Slot slot = slots.get(i);
                if (slot instanceof SlotFalseCopy || !slot.mayPlace(stack)) {
                    i += iterOrder;
                    continue;
                }
                ItemStack stackInSlot = slot.getItem();
                if (!stackInSlot.isEmpty() && itemsEqualWithTags(stackInSlot, stack)) {
                    int size = stackInSlot.getCount() + stack.getCount();
                    int maxSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    if (size <= maxSize) {
                        stack.setCount(0);
                        stackInSlot.setCount(size);
                        slot.set(stackInSlot);
                        successful = true;
                    } else if (stackInSlot.getCount() < maxSize) {
                        stack.shrink(maxSize - stackInSlot.getCount());
                        stackInSlot.setCount(maxSize);
                        slot.set(stackInSlot);
                        successful = true;
                    }
                }
                i += iterOrder;
            }
        }
        if (!stack.isEmpty()) {
            i = reverseDirection ? endIndex - 1 : startIndex;
            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }
                Slot slot = slots.get(i);
                if (slot instanceof SlotFalseCopy) {
                    i += iterOrder;
                    continue;
                }
                ItemStack stackInSlot = slot.getItem();
                if (stackInSlot.isEmpty() && slot.mayPlace(stack)) {
                    int maxSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    int splitSize = Math.min(maxSize, stack.getCount());
                    slot.set(stack.split(splitSize));
                    successful = true;
                }
                i += iterOrder;
            }
        }
        return successful;
    }

    // region BLOCK TRANSFER
    public static boolean extractFromAdjacent(BlockEntity tile, ItemStorageCoFH slot, int amount, Direction side) {

        BlockEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
        Direction opposite = side.getOpposite();

        if (hasItemHandlerCap(adjTile, opposite)) {
            IItemHandler handler = getItemHandlerCap(adjTile, opposite);
            if (handler == EmptyHandler.INSTANCE) {
                return false;
            }
            int initialAmount = amount;
            for (int i = 0; i < handler.getSlots() && amount > 0; ++i) {
                ItemStack query = handler.extractItem(i, amount, true);
                if (query.isEmpty()) {      // Skip empty slots.
                    continue;
                }
                ItemStack ret = slot.insertItem(0, query, true);
                if (ret.getCount() != query.getCount()) {       // If the slot accepted items.
                    slot.insertItem(0, handler.extractItem(i, amount, false), false);
                    amount -= query.getCount() - ret.getCount();
                }
            }
            return amount != initialAmount;
        }
        return false;
    }

    public static boolean insertIntoAdjacent(BlockEntity tile, ItemStorageCoFH slot, int amount, Direction side) {

        if (slot.isEmpty()) {
            return false;
        }
        ItemStack initialStack = slot.getItemStack().copy();
        initialStack.setCount(Math.min(amount, initialStack.getCount()));
        BlockEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
        Direction opposite = side.getOpposite();

        if (hasItemHandlerCap(adjTile, opposite)) {
            // OPTIMIZATION: This is used instead of addToInventory because prechecks have already happened.
            ItemStack inserted = insertStackIntoInventory(getItemHandlerCap(adjTile, opposite), initialStack, false);
            if (inserted.getCount() >= initialStack.getCount()) {
                return false;
            }
            slot.modify(inserted.getCount() - initialStack.getCount());
            return true;
        }
        return false;
    }
    // endregion

    // region HELPERS
    public static ItemStack addToInventory(BlockEntity tile, Direction side, ItemStack stack) {

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (hasItemHandlerCap(tile, side.getOpposite())) {
            stack = insertStackIntoInventory(getItemHandlerCap(tile, side.getOpposite()), stack, false);
        }
        return stack;
    }

    public static boolean hasItemHandlerCap(BlockEntity tile, Direction face) {

        return tile != null && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).isPresent() || tile instanceof Container;
    }

    public static IItemHandler getItemHandlerCap(BlockEntity tile, Direction face) {

        if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).isPresent()) {
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face).orElse(EmptyHandler.INSTANCE);
        } else if (tile instanceof WorldlyContainer && face != null) {
            return new SidedInvWrapper(((WorldlyContainer) tile), face);
        } else if (tile instanceof Container) {
            return new InvWrapper((Container) tile);
        }
        return EmptyHandler.INSTANCE;
    }

    public static boolean isEmpty(ItemStack[] inventory) {

        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    // endregion
}

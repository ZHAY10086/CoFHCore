package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;

import static cofh.lib.util.Constants.CMD_PLAYER;

public class SubCommandEnderChest {

    public static int permissionLevel = 2;

    static final TranslatableComponent TITLE = new TranslatableComponent("container.enderchest");

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("enderchest")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self
                .executes(context -> openContainer(context.getSource().getPlayerOrException(), context.getSource().getPlayerOrException()))
                // Target Specified
                .then(Commands.argument(CMD_PLAYER, EntityArgument.player())
                        .executes(context -> openContainer(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, CMD_PLAYER))));
    }

    private static int openContainer(Player user, Player target) {

        MutableComponent title = TITLE.copy();

        if (user != target) {
            title.append(" - ").append(target.getDisplayName());
        }
        user.openMenu(new SimpleMenuProvider((id, player, inv) -> ChestMenu.threeRows(id, player, target.getEnderChestInventory()), title));
        user.awardStat(Stats.OPEN_ENDERCHEST);
        return 1;
    }

}

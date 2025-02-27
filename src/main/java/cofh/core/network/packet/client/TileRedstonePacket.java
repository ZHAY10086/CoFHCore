package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.api.block.entity.IPacketHandlerTile;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.Utils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.core.network.packet.PacketIDs.PACKET_REDSTONE;
import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;

public class TileRedstonePacket extends PacketBase implements IPacketClient {

    protected BlockPos pos;
    protected FriendlyByteBuf buffer;

    public TileRedstonePacket() {

        super(PACKET_REDSTONE, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Level world = ProxyUtils.getClientWorld();
        if (world == null) {
            CoFHCore.LOG.error("Client world is null! (Is this being called on the server?)");
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IPacketHandlerTile handlerTile) {
            handlerTile.handleRedstonePacket(buffer);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeBytes(buffer);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        buffer = buf;
        pos = buffer.readBlockPos();
    }

    public static void sendToClient(IPacketHandlerTile tile) {

        if (tile.world() == null || Utils.isClientWorld(tile.world())) {
            return;
        }
        TileRedstonePacket packet = new TileRedstonePacket();
        packet.pos = tile.pos();
        packet.buffer = tile.getRedstonePacket(new FriendlyByteBuf(Unpooled.buffer()));
        packet.sendToAllAround(packet.pos, NETWORK_UPDATE_DISTANCE, tile.world().dimension());
    }

}

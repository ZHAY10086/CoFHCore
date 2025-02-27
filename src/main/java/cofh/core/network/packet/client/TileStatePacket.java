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
import net.minecraft.world.level.block.state.BlockState;

import static cofh.core.network.packet.PacketIDs.PACKET_STATE;
import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;

public class TileStatePacket extends PacketBase implements IPacketClient {

    protected BlockPos pos;
    protected FriendlyByteBuf buffer;

    public TileStatePacket() {

        super(PACKET_STATE, CoFHCore.PACKET_HANDLER);
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
            handlerTile.handleStatePacket(buffer);
            BlockState state = tile.getLevel().getBlockState(pos);
            tile.getLevel().sendBlockUpdated(pos, state, state, 3);
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
        TileStatePacket packet = new TileStatePacket();
        packet.pos = tile.pos();
        packet.buffer = tile.getStatePacket(new FriendlyByteBuf(Unpooled.buffer()));
        packet.sendToAllAround(packet.pos, NETWORK_UPDATE_DISTANCE, tile.world().dimension());
    }

}

package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public class PipeUpgradeContainerMenu extends BaseContainerMenu {

    private final ItemStackHandler itemStackHandler;
    public final PipeUpgradeManager upgradeManager;
    public final PipeIoMode pipeIoMode;

    public PipeUpgradeContainerMenu(int id, Player player, ItemStackHandler itemStackHandler, FriendlyByteBuf data) {
        this(id, player, itemStackHandler, data.readBlockPos(), Direction.values()[data.readByte()], PipeIoMode.values()[data.readByte()]);
    }

    public PipeUpgradeContainerMenu(int id, Player player, ItemStackHandler itemStackHandler, BlockPos pos, Direction dir, PipeIoMode pipeIoMode) {
        super(FluxPylonsContainerMenus.PIPE_UPGRADE_CONTAINER_MENU.get(), id, player);
        this.itemStackHandler = itemStackHandler;
        this.pipeIoMode = pipeIoMode;
        this.upgradeManager = player.level.isClientSide() ? null : NetworkManager.get(player.level).getNode(pos).getUpgradeManager(dir);

        this.addOwnSlots();
        this.addPlayerInventory();
    }

    @Override
    protected int getSlotCount() {
        return 10;
    }

    @Override
    protected Pair<Integer, Integer> getPlayerInventoryPosition() {
        return Pair.of(8, 71);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        var itemStack = super.quickMoveStack(player, slotIndex);
        
        if (this.upgradeManager != null) {
            this.upgradeManager.pipeUpgradeContainer.setChanged();
        }
        
        return itemStack;
    }

    protected void addOwnSlots() {
        var off = 18 * 2;
        var y = 18;
        
        var slot = -1;
        
        for (var i = 0; i < this.itemStackHandler.getSlots() / 5; i++) {
            for (var j = 0; j < 5; j++) {
                slot++;
                this.addSlot(new SlotItemHandler(this.itemStackHandler, slot, 8 + off + j * 18, y + i * 18));
            }
        }
    }
}

package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PipeUpgradeContainerMenu extends BaseContainerMenu {

    private final ItemStackHandler itemStackHandler;

    public PipeUpgradeContainerMenu(int id, Player player, ItemStackHandler itemStackHandler) {
        super(FluxPylonsContainerMenus.PIPE_UPGRADE_CONTAINER_MENU, id, player, 10);
        this.itemStackHandler = itemStackHandler;
        
        this.addOwnSlots();
        this.addPlayerInventory(8, 71);
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
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

package com.joelcrosby.fluxpylons.item;

import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class UpgradeContainerMenu extends BaseContainerMenu {

    private final ItemStackHandler itemStackHandler;

    public UpgradeContainerMenu(@Nullable MenuType<?> type, int id, Player player, BlockPos pos, ItemStackHandler itemStackHandler) {
        super(type, id, player, 10);
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

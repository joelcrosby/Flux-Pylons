package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class CrateContainerMenu extends BaseContainerMenu {

    public final CrateBlockEntity tile;
    
    public CrateContainerMenu(@Nullable MenuType<?> type, int id, Player player, BlockPos pos) {
        super(type, id, player, 54);
        
        this.tile = Utility.getBlockEntity(CrateBlockEntity.class, player.level, pos);

        this.addOwnSlots();
        this.addPlayerInventory(8, 140);
    }

    protected void addOwnSlots() {
        var y = 18;
        
        var slot = -1;
        
        for (var i = 0; i < this.tile.items.getSlots() / 9; i++) {
            for (var j = 0; j < 9; j++) {
                slot++;
                this.addSlot(new SlotItemHandler(this.tile.items, slot, 8 + j * 18, y + i * 18));
            }
        }
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

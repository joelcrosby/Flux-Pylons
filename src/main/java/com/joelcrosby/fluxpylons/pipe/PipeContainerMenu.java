package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class PipeContainerMenu extends BaseContainerMenu {

    public final PipeBlockEntity tile;
    
    public PipeContainerMenu(@Nullable MenuType<?> type, int id, Player player, BlockPos pos) {
        super(type, id, player, 10);
        
        this.tile = Utility.getBlockEntity(PipeBlockEntity.class, player.level, pos);

        this.addOwnSlots();
        this.addPlayerInventory(8, 71);
    }

    protected void addOwnSlots() {
        var off = 18 * 2;
        var y = 18;
        
        var slot = -1;
        
        for (var i = 0; i < this.tile.items.getSlots() / 5; i++) {
            for (var j = 0; j < 5; j++) {
                slot++;
                this.addSlot(new SlotItemHandler(this.tile.items, slot, 8 + off + j * 18, y + i * 18));
            }
        }
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

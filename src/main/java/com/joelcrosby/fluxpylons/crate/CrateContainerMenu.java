package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class CrateContainerMenu extends AbstractContainerMenu {

    public final CrateBlockEntity tile;
    
    public CrateContainerMenu(@Nullable MenuType<?> type, int id, Player player, BlockPos pos) {
        super(type, id);
        this.tile = Utility.getBlockEntity(CrateBlockEntity.class, player.level, pos);

        this.addOwnSlots();
        this.addPlayerInventory(player);
    }

    protected void addOwnSlots() {
        var off = this.getSlotXOffset();
        var y = 18;
        
        var slot = -1;
        
        for (var i = 0; i < this.tile.items.getSlots() / 9; i++) {
            for (var j = 0; j < 9; j++) {
                slot++;
                this.addSlot(new SlotItemHandler(this.tile.items, slot, 8 + off + j * 18, y + i * 18));
            }
        }
    }
    
    protected void addPlayerInventory(Player player) {
        var off = this.getSlotXOffset();
        
        var y = 140;
        var hby = y + 58;
        
        for (var l = 0; l < 3; ++l)
            for (var j1 = 0; j1 < 9; ++j1)
                this.addSlot(new Slot(player.getInventory(), j1 + l * 9 + 9, 8 + off + j1 * 18, y + l * 18));
        for (var i1 = 0; i1 < 9; ++i1)
            this.addSlot(new Slot(player.getInventory(), i1, 8 + off + i1 * 18, hby));
    }

    protected int getSlotXOffset() {
        return 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return Utility.transferStackInSlot(this, this::moveItemStackTo, player, slotIndex, stack -> Pair.of(0, 54));
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

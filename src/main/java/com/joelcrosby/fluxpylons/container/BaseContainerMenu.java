package com.joelcrosby.fluxpylons.container;

import com.joelcrosby.fluxpylons.Utility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class BaseContainerMenu extends AbstractContainerMenu {
    private final Player player;
    private final int slots;

    protected BaseContainerMenu(@Nullable MenuType<?> type, int windowId, Player player, int slots) {
        super(type, windowId);

        this.player = player;
        this.slots = slots;
    }
    
    protected void addPlayerInventory(int xInventory, int yInventory) {
        var id = 9;

        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 9; x++) {
                addSlot(new Slot(player.getInventory(), id, xInventory + x * 18, yInventory + y * 18));

                id++;
            }
        }

        id = 0;

        for (int i = 0; i < 9; i++) {
            var x = xInventory + i * 18;
            var y = yInventory + 4 + (3 * 18);

            addSlot(new Slot(player.getInventory(), id, x, y));

            id++;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return Utility.transferStackInSlot(this, this::moveItemStackTo, player, slotIndex, stack -> Pair.of(0, this.slots));
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

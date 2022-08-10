package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.lang3.tuple.Pair;

public abstract class MachineContainerMenu<T extends MachineBlockEntity> extends BaseContainerMenu {

    public final T tile;

    public MachineContainerMenu(Class<T> entityClass, MenuType<?> menuType, int id, Player player, BlockPos pos) {
        super(menuType, id, player);

        this.tile = Utility.getBlockEntity(entityClass, player.level, pos);

        this.addOwnSlots();
        this.addPlayerInventory();
    }

    @Override
    protected int getSlotCount() {
        return 9;
    }

    @Override
    protected Pair<Integer, Integer> getPlayerInventoryPosition() {
        return Pair.of(8, 84);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public abstract void addOwnSlots();
}

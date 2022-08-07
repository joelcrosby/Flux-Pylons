package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import com.joelcrosby.fluxpylons.container.BaseEnergySlot;
import com.joelcrosby.fluxpylons.container.BaseInputSlot;
import com.joelcrosby.fluxpylons.container.BaseOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public class SmelterContainerMenu extends BaseContainerMenu {

    public final SmelterBlockEntity tile;

    public SmelterContainerMenu(int id, Player player, BlockPos pos) {
        super(FluxPylonsContainerMenus.SMELTER_CONTAINER_MENU, id, player);

        this.tile = Utility.getBlockEntity(SmelterBlockEntity.class, player.level, pos);

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

    protected void addOwnSlots() {
        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            addSlot(new BaseInputSlot(handler, 0, 30, 25));
            addSlot(new BaseInputSlot(handler, 1, 48, 25));
            addSlot(new BaseInputSlot(handler, 2, 66, 25));
            
            addSlot(new BaseInputSlot(handler, 3, 30, 43));
            addSlot(new BaseInputSlot(handler, 4, 48, 43));
            addSlot(new BaseInputSlot(handler, 5, 66, 43));
            
            addSlot(new BaseOutputSlot(handler, 6, 128, 35));
            addSlot(new BaseOutputSlot(handler, 7, 148, 35));
            
            addSlot(new BaseEnergySlot(handler, 8, 8, 53));
        });
    }
}

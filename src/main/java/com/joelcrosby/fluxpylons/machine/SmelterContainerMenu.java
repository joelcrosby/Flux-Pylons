package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseEnergySlot;
import com.joelcrosby.fluxpylons.container.BaseInputSlot;
import com.joelcrosby.fluxpylons.container.BaseOutputSlot;
import com.joelcrosby.fluxpylons.machine.common.MachineContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.CapabilityItemHandler;

public class SmelterContainerMenu extends MachineContainerMenu<SmelterBlockEntity> {

    public SmelterContainerMenu(int id, Player player, BlockPos pos) {
        super(SmelterBlockEntity.class, FluxPylonsContainerMenus.SMELTER_CONTAINER_MENU, id, player, pos);
    }

    @Override
    protected int getSlotCount() {
        return 9;
    }

    @Override
    public void addOwnSlots() {
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

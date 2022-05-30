package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends BaseContainerBlockEntity implements MenuProvider {

    private final static int SIZE = 54;
    
    public final ItemStackHandler items = new ItemStackHandler(SIZE);
    private LazyOptional<IItemHandlerModifiable> handler;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.CRATE, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container." + FluxPylons.ID + ".crate");
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container." + FluxPylons.ID + ".crate");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new CrateContainerMenu(FluxPylonsContainerMenus.CRATE_CONTAINER_MENU, window, player, worldPosition);
    }

    @Override
    protected AbstractContainerMenu createMenu(int window, Inventory inventory) {
        return new CrateContainerMenu(FluxPylonsContainerMenus.CRATE_CONTAINER_MENU, window, null, worldPosition);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (var i = 0; i < items.getSlots(); i++) {
            var inSlot = items.getStackInSlot(i);
            
            if (!inSlot.isEmpty()) return false;
        }
        
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.extractItem(slot, items.getStackInSlot(slot).getCount(), true);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (var i = 0; i < this.items.getSlots(); i++)
            this.items.setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("items", this.items.serializeNBT());
    }

    @Override
    public void load(CompoundTag compound) {
        this.items.deserializeNBT(compound.getCompound("items"));
        super.load(compound);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.handler == null)
                this.handler = LazyOptional.of(this::createHandler);
            return this.handler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }
}

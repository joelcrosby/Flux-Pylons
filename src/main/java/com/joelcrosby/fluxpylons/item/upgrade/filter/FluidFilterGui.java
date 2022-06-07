package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.gui.ToggleButton;
import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.network.packets.PacketGhostSlot;
import com.joelcrosby.fluxpylons.network.packets.PacketUpdateFluidFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class FluidFilterGui extends AbstractContainerScreen<FluidFilterContainerMenu> {
    private final FluidFilterContainerMenu container;
    private static final ResourceLocation TEXTURE = new ResourceLocation(FluxPylons.ID, "textures/gui/filter.png");
    private final ItemStack filterItem;
    
    private boolean isDenyFilter;
    
    public FluidFilterGui(FluidFilterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        
        this.imageWidth = 176;
        this.imageHeight = 153;
        this.container = container;
        this.filterItem = container.filterItem;
    }

    @Override
    protected void init() {
        super.init();

        var allowDenyTextures = new ResourceLocation[] {
                new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_allow.png"),
                new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_deny.png"),
        };

        isDenyFilter = FilterItem.getIsDenyFilter(filterItem);

        var allowDenyX = getGuiLeft() + 8;
        var allowDenyY = getGuiTop() + 18;

        var allowDenyBtn = new ToggleButton(allowDenyX, allowDenyY, allowDenyTextures, isDenyFilter ? 1 : 0, (btn) -> {
            isDenyFilter = !isDenyFilter;
            ((ToggleButton) btn).setTexturePosition(isDenyFilter ? 1 : 0);
        });
        
        addRenderableWidget(allowDenyBtn);
    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PacketUpdateFluidFilter(isDenyFilter));
        super.onClose();
    }
    
    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(poseStack);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752);
        this.font.draw(poseStack, this.title.getString(), 8, 6, 4210752);

        renderTooltip(poseStack, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (hoveredSlot == null || !(hoveredSlot instanceof FilterSlotHandler)) {
            return super.mouseClicked(x, y, btn);
        }

        var stack = this.menu.getCarried();
        stack = stack.copy().split(hoveredSlot.getMaxStackSize());

        if (ItemHandlerHelper.canItemStacksStack(stack, container.filterItem)) {
            return true;
        }

        hoveredSlot.set(stack);

        PacketHandler.sendToServer(new PacketGhostSlot(hoveredSlot.index, stack, stack.getCount()));

        return true;
    }
}

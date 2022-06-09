package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.gui.ToggleButton;
import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.network.packets.PacketGhostSlot;
import com.joelcrosby.fluxpylons.network.packets.PacketUpdateFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class BaseFilterGui<TContainerMenu extends BaseFilterContainerMenu> extends AbstractContainerScreen<TContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FluxPylons.ID, "textures/gui/filter.png");
    
    protected final TContainerMenu container;
    protected final ItemStack filterItem;
    protected final BaseFilterItem item;

    protected boolean isDenyList;
    protected boolean matchNbt;
    
    public BaseFilterGui(TContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        
        this.imageWidth = 176;
        this.imageHeight = 153;
        this.container = container;
        this.filterItem = container.filterItem;
        this.item = (BaseFilterItem) filterItem.getItem();
    }

    @Override
    protected void init() {
        super.init();

        var allowDenyTextures = new ResourceLocation[] {
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_allow.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_deny.png"),
        };

        var allowDenyTooltips = new String[] {
                "item.fluxpylons.filter.tooltip.allow",
                "item.fluxpylons.filter.tooltip.deny",
        };
        
        isDenyList = BaseFilterItem.getIsDenyList(filterItem);
        
        var allowDenyX = getGuiLeft() + 8;
        var allowDenyY = getGuiTop() + 18;

        var allowDenyBtn = new ToggleButton(this, allowDenyX, allowDenyY, allowDenyTextures, allowDenyTooltips, isDenyList ? 1 : 0, (btn) -> {
            isDenyList = !isDenyList;            
            ((ToggleButton) btn).setTexturePosition(isDenyList ? 1 : 0);
        });

        var matchNbtTextures = new ResourceLocation[] {
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_match_nbt_off.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_match_nbt_on.png"),
        };
        
        var matchNbtTooltips = new String[] {
                "item.fluxpylons.filter.tooltip.ignore-nbt",
                "item.fluxpylons.filter.tooltip.match-nbt",
        };

        matchNbt = BaseFilterItem.getMatchNbt(filterItem);

        var matchNbtX = getGuiLeft() + 8;
        var matchNbtY = getGuiTop() + 36;

        var matchNbtBtn = new ToggleButton(this, matchNbtX, matchNbtY, matchNbtTextures, matchNbtTooltips, matchNbt ? 1 : 0, (btn) -> {
            matchNbt = !matchNbt;
            ((ToggleButton) btn).setTexturePosition(matchNbt ? 1 : 0);
        });

        addRenderableWidget(allowDenyBtn);
        
        if (item.supportsNbtMatch()) {
            addRenderableWidget(matchNbtBtn);
        }
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

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PacketUpdateFilter(isDenyList, matchNbt));
        super.onClose();
    }
}

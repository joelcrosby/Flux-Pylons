package com.joelcrosby.fluxpylons.gui;

import com.joelcrosby.fluxpylons.FluxPylons;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class InteractionSideButton extends ToggleButton {
    private static final ResourceLocation[] Textures = new ResourceLocation[] {
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_off.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_down.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_up.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_north.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_south.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_west.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_side_east.png"),
    };

    private static final String[] Tooltips = new String[] {
            "item.fluxpylons.filter.tooltip.interaction-side-off",
            "item.fluxpylons.filter.tooltip.interaction-side-down",
            "item.fluxpylons.filter.tooltip.interaction-side-up",
            "item.fluxpylons.filter.tooltip.interaction-side-north",
            "item.fluxpylons.filter.tooltip.interaction-side-south",
            "item.fluxpylons.filter.tooltip.interaction-side-west",
            "item.fluxpylons.filter.tooltip.interaction-side-east",
    };
    
    public InteractionSideButton(Screen screen, int x, int y, Direction direction, OnPress onPress) {
        super(screen, x, y, Textures, Tooltips, direction == null ? 0 : direction.ordinal() + 1, onPress);
    }
    
    public Direction next() {
        if (texturePosition == textures.length - 1) {
            texturePosition = 0;
        } else {
            texturePosition++;
        }
        
        if (texturePosition == 0) return null;
        
        return Direction.values()[texturePosition - 1];
    }
}

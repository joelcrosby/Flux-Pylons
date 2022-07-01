package com.joelcrosby.fluxpylons.rendering;

import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.LinkedList;
import java.util.Queue;

public class DelayedRenderer {
    private static final Queue<PylonBlockEntity> pylonRenders = new LinkedList<>();

    public static void render(PoseStack poseStack) {
        if (pylonRenders.size() > 0) {
            PylonRenderer.render(pylonRenders, poseStack);
        }
    }

    public static void add(PylonBlockEntity entity) {
        pylonRenders.add(entity);
    }
}

package org.skyfish.util;

import org.skyfish.mixin.render.RenderManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class RenderUtils {

    public static void renderBoundingBox(Entity entity, float partialTicks, int color) {
        try {
            renderBoundingBox(entity, partialTicks, color, 0.5f, new Vec3(0, 0, 0));
        } catch(Exception ignored) { }
    }

    public static void renderBoundingBox(Entity entity, float partialTicks, int color, float opacity, Vec3 offset) {
        RenderManagerAccessor rm = (RenderManagerAccessor) Minecraft.getMinecraft().getRenderManager();

        double renderPosX = rm.getRenderPosX();
        double renderPosY = rm.getRenderPosY();
        double renderPosZ = rm.getRenderPosZ();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ;

        AxisAlignedBB bbox = entity.getEntityBoundingBox();
        if (bbox.maxX - bbox.minX == 0 && bbox.maxY - bbox.minY == 0 && bbox.maxZ - bbox.minZ == 0) {
            bbox = new AxisAlignedBB(entity.posX - 0.3, entity.posY, entity.posZ - 0.3, entity.posX + 0.3, entity.posY + 1.62, entity.posZ + 0.3);
        }
        AxisAlignedBB aabb = new AxisAlignedBB(
                bbox.minX - entity.posX + x + offset.xCoord,
                bbox.minY - entity.posY + y + offset.yCoord,
                bbox.minZ - entity.posZ + z + offset.zCoord,
                bbox.maxX - entity.posX + x + offset.xCoord,
                bbox.maxY - entity.posY + y + offset.yCoord,
                bbox.maxZ - entity.posZ + z + + offset.zCoord
        );

        drawFilledBoundingBox(aabb, color, opacity);
    }

    public static void drawFilledBoundingBox(AxisAlignedBB aabb, int color, float opacity) {
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.color(r, g, b, a * opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();
        GlStateManager.color(r, g, b, a * opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        GlStateManager.color(r, g, b, a * opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        tessellator.draw();
        GlStateManager.color(r, g, b, a);
        RenderGlobal.drawSelectionBoundingBox(aabb);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
package mod.chiselsandbits.mixin.compat.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.compat.client.DrawSelectionEvents;
import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @ModifyArg(
            method = "renderHitOutline",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"),
            index = 2)
    private VoxelShape chiselsandbits$renderHitOutline(
            VoxelShape voxelShape, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Entity entity) {
        if (state.getBlock() instanceof BlockChiseled) {
            ItemStack mainHeldItem =
                    entity instanceof LivingEntity livingEntity ? livingEntity.getMainHandItem() : ItemStack.EMPTY;
            if (mainHeldItem.getItem() instanceof ItemChiseledBit || mainHeldItem.getItem() instanceof ItemChisel) {
                return Shapes.empty();
            }
        }

        return voxelShape;
    }

    @WrapWithCondition(
            method = "renderLevel",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private boolean port_lib$renderBlockOutline(
            LevelRenderer self,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            Entity entity,
            double d,
            double e,
            double f,
            BlockPos blockPos,
            BlockState blockState,
            /* enclosing args */ PoseStack p,
            float partialTicks,
            long l,
            boolean bl,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f matrix4f) {
        return !DrawSelectionEvents.BLOCK
                .invoker()
                .onHighlightBlock(
                        self, camera, minecraft.hitResult, partialTicks, poseStack, renderBuffers.bufferSource());
    }

    @Inject(
            method = "renderLevel",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewStack()Lcom/mojang/blaze3d/vertex/PoseStack;",
                            shift = At.Shift.BEFORE))
    private void port_lib$renderEntityOutline(
            PoseStack poseStack,
            float partialTick,
            long finishNanoTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f projectionMatrix,
            CallbackInfo ci) {
        HitResult hitresult = minecraft.hitResult;
        if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
            DrawSelectionEvents.ENTITY
                    .invoker()
                    .onHighlightEntity(
                            (LevelRenderer) (Object) this,
                            camera,
                            hitresult,
                            partialTick,
                            poseStack,
                            this.renderBuffers.bufferSource());
        }
    }
}

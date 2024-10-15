package mod.chiselsandbits.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface TransformTypeDependentItemBakedModel {

  BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand);
}

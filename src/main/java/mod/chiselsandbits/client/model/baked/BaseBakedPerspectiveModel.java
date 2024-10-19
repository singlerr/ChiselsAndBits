package mod.chiselsandbits.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import mod.chiselsandbits.utils.TransformationUtils;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class BaseBakedPerspectiveModel implements BakedModel, TransformTypeDependentItemBakedModel {

    protected static final RandomSource RANDOM = RandomSource.create();

    private static final Transformation ground;
    private static final Transformation gui;
    private static final Transformation fixed;
    private static final Transformation firstPerson_righthand;
    private static final Transformation firstPerson_lefthand;
    private static final Transformation thirdPerson_righthand;
    private static final Transformation thirdPerson_lefthand;

    static {
        gui = getMatrix(0, 0, 0, 30, 225, 0, 0.625f);
        ground = getMatrix(0, 3 / 16.0f, 0, 0, 0, 0, 0.25f);
        fixed = getMatrix(0, 0, 0, 0, 0, 0, 0.5f);
        thirdPerson_lefthand = thirdPerson_righthand = getMatrix(0, 2.5f / 16.0f, 0, 75, 45, 0, 0.375f);
        firstPerson_righthand = firstPerson_lefthand = getMatrix(0, 0, 0, 0, 45, 0, 0.40f);
    }

    private static Transformation getMatrix(
            final float transX,
            final float transY,
            final float transZ,
            final float rotX,
            final float rotY,
            final float rotZ,
            final float scaleXYZ) {
        final Vector3f translation = new Vector3f(transX, transY, transZ);
        final Vector3f scale = new Vector3f(scaleXYZ, scaleXYZ, scaleXYZ);
        final Quaternionf rotation = TransformationUtils.quatFromXYZ(rotX, rotY, rotZ, true);
        return new Transformation(translation, rotation, scale, null);
    }

    @Override
    public ItemTransforms getTransforms() {
        return new PerspectiveItemModelDelegate(this);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
        switch (context) {
            case FIRST_PERSON_LEFT_HAND:
                TransformationUtils.push(poseStack, firstPerson_lefthand, leftHand);
                return this;
            case FIRST_PERSON_RIGHT_HAND:
                TransformationUtils.push(poseStack, firstPerson_righthand, leftHand);
                return this;
            case THIRD_PERSON_LEFT_HAND:
                TransformationUtils.push(poseStack, thirdPerson_lefthand, leftHand);
                return this;
            case THIRD_PERSON_RIGHT_HAND:
                TransformationUtils.push(poseStack, thirdPerson_righthand, leftHand);
            case FIXED:
                TransformationUtils.push(poseStack, firstPerson_righthand, leftHand);
                return this;
            case GROUND:
                TransformationUtils.push(poseStack, ground, leftHand);
                return this;
            case GUI:
                TransformationUtils.push(poseStack, gui, leftHand);
                return this;
            default:
        }

        TransformationUtils.push(poseStack, fixed, leftHand);
        return this;
    }

    private static final class PerspectiveItemModelDelegate extends ItemTransforms {

        private final TransformTypeDependentItemBakedModel delegate;

        public PerspectiveItemModelDelegate(TransformTypeDependentItemBakedModel delegate) {
            super(
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM,
                    ItemTransform.NO_TRANSFORM);
            this.delegate = delegate;
        }

        @Override
        public ItemTransform getTransform(ItemDisplayContext itemDisplayContext) {
            return new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f()) {
                @Override
                public void apply(boolean bl, PoseStack poseStack) {
                    delegate.applyTransform(itemDisplayContext, poseStack, bl);
                }
            };
        }
    }
}

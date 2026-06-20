package xyz.iwolfking.woldsvaults.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.client.render.DynamicHumanoidModelLayer;
import iskallia.vault.entity.model.FighterModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.entities.WoldBoss;

public class WoldBossRenderer extends LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>> {
    public static final ResourceLocation TEXTURE = WoldsVaults.id("textures/entity/wold.png");
    private final DynamicHumanoidModelLayer<LivingEntity, HumanoidModel<LivingEntity>, HumanoidModel<LivingEntity>> armorLayer;
    private final HumanoidModel<LivingEntity> thisModel;
    private final PlayerModel playerModel;
    private final PlayerModel playerSlimModel;

    public WoldBossRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayer) {
        this(context, modelLayer, false);
    }

    public WoldBossRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayer, boolean slim) {
        super(context, new FighterModel(context.bakeLayer(modelLayer), slim), 0.5F);
        this.addLayer(this.armorLayer = new DynamicHumanoidModelLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR))));
        this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
        this.addLayer(new ElytraLayer(this, context.getModelSet()));
        this.addLayer(new ItemInHandLayer(this));
        this.thisModel = (HumanoidModel)this.model;
        this.playerModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
        this.playerSlimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        this.armorLayer.setSlim(slim);
    }



    protected boolean shouldShowName(LivingEntity entity) {
        return entity.isCustomNameVisible() && super.shouldShowName(entity);
    }

    public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn) {
        if (entity instanceof WoldBoss woldBoss) {
            this.setModelVisibilities(woldBoss);
            this.model = this.playerModel;
            super.render(woldBoss, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
        }
    }

    public Vec3 getRenderOffset(WoldBoss entityIn, float partialTicks) {
        return entityIn.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entityIn, partialTicks);
    }

    private void setModelVisibilities(LivingEntity entity) {
        if (entity instanceof WoldBoss woldBoss) {
            setVisibilities(this.playerModel, woldBoss);
            }
    }


    private static void setVisibilities(PlayerModel<?> playerModel, LivingEntity entity) {
        playerModel.setAllVisible(true);
        playerModel.hat.visible = true;
        playerModel.jacket.visible = true;
        playerModel.leftPants.visible = true;
        playerModel.rightPants.visible = true;
        playerModel.leftSleeve.visible = true;
        playerModel.rightSleeve.visible = true;
        playerModel.crouching = entity.isCrouching();
        HumanoidModel.ArmPose mainArmPose = getArmPose(entity, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose offHandPose = getArmPose(entity, InteractionHand.OFF_HAND);
        if (mainArmPose.isTwoHanded()) {
            offHandPose = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            playerModel.rightArmPose = mainArmPose;
            playerModel.leftArmPose = offHandPose;
        } else {
            playerModel.rightArmPose = offHandPose;
            playerModel.leftArmPose = mainArmPose;
        }

    }

    private static HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand) {
        ItemStack itemstack = entity.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
                UseAnim useaction = itemstack.getUseAnimation();
                if (useaction == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useaction == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useaction == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useaction == UseAnim.CROSSBOW && hand == entity.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else if (!entity.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public ResourceLocation getTextureLocation(LivingEntity entity) {
        if (entity instanceof WoldBoss woldBoss) {
            return TEXTURE;
        } else {
            return MissingTextureAtlasSprite.getLocation();
        }
    }

    protected void setupRotations(LivingEntity entity, PoseStack matrixStack, float age, float yaw, float pTicks) {
        float f = entity.getSwimAmount(pTicks);
        float f3;
        float f2;
        if (entity.isFallFlying()) {
            super.setupRotations(entity, matrixStack, age, yaw, pTicks);
            f3 = (float)entity.getFallFlyingTicks() + pTicks;
            f2 = Mth.clamp(f3 * f3 / 100.0F, 0.0F, 1.0F);
            if (!entity.isAutoSpinAttack()) {
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entity.getXRot())));
            }

            Vec3 vec3 = entity.getViewVector(pTicks);
            Vec3 vec31 = entity.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                matrixStack.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(entity, matrixStack, age, yaw, pTicks);
            f3 = entity.isInWater() ? -90.0F - entity.getXRot() : -90.0F;
            f2 = Mth.lerp(f, 0.0F, f3);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2));
            if (entity.isVisuallySwimming()) {
                matrixStack.translate(0.0, -1.0, 0.30000001192092896);
            }
        } else {
            super.setupRotations(entity, matrixStack, age, yaw, pTicks);
        }

    }


    @Override
    protected void scale(LivingEntity p_115314_, PoseStack poseStack, float p_115316_) {
        super.scale(p_115314_, poseStack, p_115316_);
        poseStack.scale(2.0F, 2.0F, 2.0F);
    }
}

package io.github.a1qs.vaultadditions.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ShieldWallParticle extends TextureSheetParticle {
    private Entity entity = null;
    private final double radius;
    private final double rotation;
    private int ticks;
    private final SpriteSet sprites;

    protected ShieldWallParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.radius = pXSpeed;
        this.rotation = pYSpeed;
        Vec3 offset = new Vec3(this.radius * Math.cos(this.rotation), (double)0.0F, this.radius * Math.sin(this.rotation));
        this.x = pX + offset.x();
        this.y = pY + offset.y() + (double)1.15F;
        this.z = pZ + offset.z();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.gravity = -0.01F;
        this.friction = 0.9F;
        this.setSprite(pSprites.get(this.random));
        this.scale(0.5F);
        this.setColor(1.0F, 0.95F, 0.6F);
        this.sprites = pSprites;
        this.ticks = 0;
        this.xd = (double)0.0F;
        this.yd = (double)0.0F;
        this.zd = (double)0.0F;
        this.lifetime = 30;
        this.quadSize = 0.15F;
        this.hasPhysics = false;
    }

    protected int getLightColor(float pPartialTick) {
        return 15728880;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setPosO() {
        if (this.entity != null) {
            Vec3 offset = new Vec3(this.radius * Math.cos(this.rotation), (double)0.0F, this.radius * Math.sin(this.rotation));
            this.x = this.entity.getX() + offset.x();
            this.y = this.entity.getY() + (double)0.15F;
            this.z = this.entity.getZ() + offset.z();
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
        }

    }

    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if (this.entity != null && this.ticks > 2) {
            super.render(pBuffer, pRenderInfo, pPartialTicks);
        }

    }

    public boolean shouldCull() {
        return false;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        ++this.ticks;
        if (this.entity != null) {
            Vec3 offset = new Vec3(this.radius * Math.cos(this.rotation), 0.0F, this.radius * Math.sin(this.rotation));
            this.x = this.entity.getX() + offset.x();
            this.y = this.entity.getY() + (double)1.0F;
            this.z = this.entity.getZ() + offset.z();
        }

        this.alpha = Mth.clamp((float)(this.lifetime - this.age) / (float)this.lifetime, 0.0F, 0.75F);
        this.setSpriteFromAge(this.sprites);
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

    }

    @Nonnull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ShieldWallParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}

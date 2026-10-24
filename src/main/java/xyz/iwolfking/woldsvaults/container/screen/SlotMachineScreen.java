package xyz.iwolfking.woldsvaults.container.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.container.SlotMachineContainer;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.ServerboundClaimSlotMachineMessage;

import java.util.List;

public class SlotMachineScreen extends AbstractContainerScreen<SlotMachineContainer> {
    private static final int[] REEL_STOP_TICKS = {65, 80, 95};
    private static final int ITEM_SPACING = 24;
    private int rollTick;
    private int lastSoundStep = -1;
    private boolean claimSent;

    public SlotMachineScreen(SlotMachineContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 184;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.rollTick++;
        int soundStep = 0;
        for (int reel = 0; reel < 3; reel++) {
            soundStep += (int)Math.floor(this.getReelPosition(reel, this.rollTick));
        }
        if (soundStep != this.lastSoundStep && this.rollTick <= REEL_STOP_TICKS[2]) {
            this.lastSoundStep = soundStep;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_HAT, 1.35F));
        }
        if (this.rollTick >= SlotMachineContainer.ROLL_TICKS && !this.claimSent) {
            this.claimSent = true;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.0F));
            ModNetwork.sendToServer(new ServerboundClaimSlotMachineMessage());
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.rollTick >= SlotMachineContainer.ROLL_TICKS;
    }

    @Override
    public void onClose() {
        if (this.rollTick >= SlotMachineContainer.ROLL_TICKS) super.onClose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        fill(poseStack, left, top, left + this.imageWidth, top + this.imageHeight, 0xEE18121F);
        fill(poseStack, left + 6, top + 6, left + this.imageWidth - 6, top + this.imageHeight - 6, 0xFF5A3A12);
        fill(poseStack, left + 10, top + 10, left + this.imageWidth - 10, top + this.imageHeight - 10, 0xFF17131B);

        List<ItemStack> pool = SlotMachineContainer.getRewardPool();
        for (int reel = 0; reel < 3; reel++) {
            int x = left + 31 + reel * 48;
            int centerY = top + 88;
            int reelTop = centerY - 60;
            int reelBottom = centerY + 60;
            fill(poseStack, x - 7, reelTop - 3, x + 25, reelBottom + 3, 0xFFF2D27B);
            fill(poseStack, x - 4, reelTop, x + 22, reelBottom, 0xFF29222F);

            this.enableReelScissor(x - 4, reelTop, x + 22, reelBottom);
            double position = this.getReelPosition(reel, this.rollTick + partialTick);
            int baseIndex = (int)Math.floor(position);
            double fraction = position - baseIndex;
            for (int offset = -3; offset <= 3; offset++) {
                int itemIndex = Math.floorMod(baseIndex + offset, pool.size());
                int itemY = centerY - 8 + (int)Math.round((offset - fraction) * ITEM_SPACING);
                ItemStack shown = pool.get(itemIndex);
                this.itemRenderer.renderAndDecorateItem(shown, x + 1, itemY);
                this.itemRenderer.renderGuiItemDecorations(this.font, shown, x + 1, itemY);
            }
            RenderSystem.disableScissor();
        }

        // The highlighted middle row is the item that will actually be awarded.
        fill(poseStack, left + 17, top + 76, left + 159, top + 77, 0xFFFFE394);
        fill(poseStack, left + 17, top + 100, left + 159, top + 101, 0xFFFFE394);

        String status = this.rollTick <= REEL_STOP_TICKS[2] ? "Rolling..." : "Winner!";
        this.font.drawShadow(poseStack, status, left + (this.imageWidth - this.font.width(status)) / 2.0F,
                top + 158, 0xFFF4D77D);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.drawShadow(poseStack, this.title, (this.imageWidth - this.font.width(this.title)) / 2.0F, 10, 0xFFF4D77D);
    }

    private double getReelPosition(int reel, double tick) {
        List<ItemStack> pool = SlotMachineContainer.getRewardPool();
        int targetIndex = 0;
        ItemStack reward = this.menu.getRewards().get(reel);
        for (int i = 0; i < pool.size(); i++) {
            if (ItemStack.isSameItemSameTags(pool.get(i), reward) && pool.get(i).getCount() == reward.getCount()) {
                targetIndex = i;
                break;
            }
        }

        int totalSteps = pool.size() * (5 + reel) + targetIndex;
        double progress = Math.min(1.0D, Math.max(0.0D, tick / REEL_STOP_TICKS[reel]));
        double remaining = 1.0D - progress;
        double easedProgress = 1.0D - remaining * remaining * remaining;
        return totalSteps * easedProgress;
    }

    private void enableReelScissor(int left, int top, int right, int bottom) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int scissorX = (int)Math.floor(left * scale);
        int scissorY = (int)Math.floor((this.height - bottom) * scale);
        int scissorWidth = (int)Math.ceil((right - left) * scale);
        int scissorHeight = (int)Math.ceil((bottom - top) * scale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }
}

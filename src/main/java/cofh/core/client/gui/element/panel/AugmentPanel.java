package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.CoreTextures;
import cofh.core.client.gui.IGuiAccess;
import cofh.core.client.gui.element.ElementAugmentSlots;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.inventory.container.slot.SlotCoFH;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.IntSupplier;

import static cofh.lib.util.helpers.StringHelper.localize;

public class AugmentPanel extends PanelBase {

    public static int defaultSide = RIGHT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x101010;
    public static int defaultBackgroundColor = 0x089e4c;

    private final int slotsBorderX1 = 18;
    private final int slotsBorderX2 = slotsBorderX1 + 60;
    private final int slotsBorderY1 = 20;
    private final int slotsBorderY2 = slotsBorderY1 + 60;

    public AugmentPanel(IGuiAccess gui, @Nonnull IntSupplier numSlots, @Nonnull List<SlotCoFH> augmentSlots) {

        this(gui, defaultSide, numSlots, augmentSlots);
    }

    protected AugmentPanel(IGuiAccess gui, int sideIn, @Nonnull IntSupplier numSlots, @Nonnull List<SlotCoFH> augmentSlots) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 102;

        addElement(new ElementAugmentSlots(gui, 24, 24, numSlots, augmentSlots));
    }

    @Override
    protected void drawForeground(PoseStack matrixStack) {

        drawPanelIcon(matrixStack, CoreTextures.ICON_AUGMENT);
        if (!fullyOpen) {
            return;
        }
        getFontRenderer().drawShadow(matrixStack, localize("info.cofh.augmentation"), sideOffset() + 18, 6, headerColor);

        RenderHelper.resetShaderColor();
    }

    @Override
    protected void drawBackground(PoseStack poseStack) {

        super.drawBackground(poseStack);

        if (!fullyOpen) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        RenderHelper.setPosTexShader();
        RenderSystem.setShaderColor(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(poseStack, sideOffset() + slotsBorderX1, slotsBorderY1, 16, 20, slotsBorderX2 - slotsBorderX1, slotsBorderY2 - slotsBorderY1);
        RenderHelper.resetShaderColor();
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(new TranslatableComponent("info.cofh.augmentation"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (!fullyOpen) {
            return false;
        }
        double x = mouseX - this.posX();
        double y = mouseY - this.posY();

        return x >= slotsBorderX1 + sideOffset() && x < slotsBorderX2 + sideOffset() && y >= slotsBorderY1 && y < slotsBorderY2;
    }

}

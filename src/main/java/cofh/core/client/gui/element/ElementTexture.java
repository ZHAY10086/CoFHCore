package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Basic element which can render an arbitrary texture.
 *
 * @author King Lemming
 */
public class ElementTexture extends ElementBase {

    protected int texU = 0;
    protected int texV = 0;

    public ElementTexture(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementTexture setUV(int u, int v) {

        texU = u;
        texV = v;
        return this;
    }

    @Override
    public void drawBackground(PoseStack matrixStack, int mouseX, int mouseY) {

        RenderHelper.setPosTexShader();
        RenderHelper.setShaderTexture0(texture);
        drawTexturedModalRect(matrixStack, posX(), posY(), texU, texV, width, height);
    }

}

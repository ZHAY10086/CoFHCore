package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.client.gui.element.ElementButton;
import cofh.core.client.gui.element.SimpleTooltip;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.api.control.ISecurable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.UUID;

import static cofh.core.client.gui.CoreTextures.*;
import static cofh.lib.api.control.ISecurable.AccessMode.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static cofh.lib.util.helpers.SoundHelper.playClickSound;
import static cofh.lib.util.helpers.StringHelper.localize;

public class SecurityPanel extends PanelBase {

    public static final String TEX_ACCESS_PUBLIC = ID_COFH_CORE + ":textures/gui/elements/button_access_public.png";
    public static final String TEX_ACCESS_PRIVATE = ID_COFH_CORE + ":textures/gui/elements/button_access_private.png";
    public static final String TEX_ACCESS_FRIENDS = ID_COFH_CORE + ":textures/gui/elements/button_access_friends.png";
    public static final String TEX_ACCESS_TEAM = ID_COFH_CORE + ":textures/gui/elements/button_access_team.png";

    public static int defaultSide = LEFT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0xf0f0f0;
    public static int defaultBackgroundColor = 0x50b050;

    private final ISecurable mySecurable;
    private final UUID myPlayer;

    public SecurityPanel(IGuiAccess gui, ISecurable securable, UUID playerID) {

        this(gui, defaultSide, securable, playerID);
    }

    protected SecurityPanel(IGuiAccess gui, int sideIn, ISecurable securable, UUID playerID) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 112;
        mySecurable = securable;
        myPlayer = playerID;

        this.setVisible(mySecurable::hasSecurity);

        addElement(new ElementButton(gui, 37, 21) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                mySecurable.setAccess(PUBLIC);
                playClickSound(0.5F);
                return true;
            }
        }
                .setSize(18, 18)
                .setTexture(TEX_ACCESS_PUBLIC, 54, 18)
                .setTooltipFactory(new SimpleTooltip(new TranslatableComponent("info.cofh.access_public")))
                .setEnabled(() -> mySecurable.getAccess() != PUBLIC));

        addElement(new ElementButton(gui, 57, 21) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                mySecurable.setAccess(PRIVATE);
                playClickSound(0.8F);
                return true;
            }
        }
                .setSize(18, 18)
                .setTexture(TEX_ACCESS_PRIVATE, 54, 18)
                .setTooltipFactory(new SimpleTooltip(new TranslatableComponent("info.cofh.access_private")))
                .setEnabled(() -> mySecurable.getAccess() != PRIVATE));

        addElement(new ElementButton(gui, 37, 41) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                mySecurable.setAccess(FRIENDS);
                playClickSound(0.6F);
                return true;
            }
        }
                .setSize(18, 18)
                .setTexture(TEX_ACCESS_FRIENDS, 54, 18)
                .setTooltipFactory(new SimpleTooltip(new TranslatableComponent("info.cofh.access_friends")))
                .setEnabled(() -> mySecurable.getAccess() != FRIENDS));

        addElement(new ElementButton(gui, 57, 41) {

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

                mySecurable.setAccess(TEAM);
                playClickSound(0.7F);
                return true;
            }
        }
                .setSize(18, 18)
                .setTexture(TEX_ACCESS_TEAM, 54, 18)
                .setTooltipFactory(new SimpleTooltip(new TranslatableComponent("info.cofh.access_team")))
                .setEnabled(() -> mySecurable.getAccess() != TEAM));

        tooltip = (element, mouseX, mouseY) -> {

            ArrayList<Component> tooltipList = new ArrayList<>();

            if (!fullyOpen) {
                tooltipList.add(new TranslatableComponent("info.cofh.owner").append(new TextComponent(": " + mySecurable.getOwnerName())));
                switch (mySecurable.getAccess()) {
                    case PUBLIC:
                        tooltipList.add(new TranslatableComponent("info.cofh.access_public").withStyle(ChatFormatting.YELLOW));
                        break;
                    case PRIVATE:
                        tooltipList.add(new TranslatableComponent("info.cofh.access_private").withStyle(ChatFormatting.YELLOW));
                        break;
                    case FRIENDS:
                        tooltipList.add(new TranslatableComponent("info.cofh.access_friends").withStyle(ChatFormatting.YELLOW));
                        break;
                    case TEAM:
                        tooltipList.add(new TranslatableComponent("info.cofh.access_team").withStyle(ChatFormatting.YELLOW));
                        break;
                }
            }
            return tooltipList;
        };
    }

    @Override
    protected void drawBackground(PoseStack poseStack) {

        switch (mySecurable.getAccess()) {
            case PUBLIC:
                backgroundColor = 0x40a040;
                break;
            case PRIVATE:
                backgroundColor = 0xa04040;
                break;
            case FRIENDS:
                backgroundColor = 0xa0a040;
                break;
            case TEAM:
                backgroundColor = 0x90b040;
                break;
        }
        super.drawBackground(poseStack);

        if (!fullyOpen) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        RenderHelper.setPosTexShader();
        RenderSystem.setShaderColor(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(poseStack, 34, 18, 16, 20, 44, 44);
        RenderHelper.resetShaderColor();
    }

    @Override
    protected void drawForeground(PoseStack matrixStack) {

        switch (mySecurable.getAccess()) {
            case PUBLIC:
                drawPanelIcon(matrixStack, ICON_ACCESS_PUBLIC);
                break;
            case PRIVATE:
                drawPanelIcon(matrixStack, ICON_ACCESS_PRIVATE);
                break;
            case FRIENDS:
                drawPanelIcon(matrixStack, ICON_ACCESS_FRIENDS);
                break;
            case TEAM:
                drawPanelIcon(matrixStack, ICON_ACCESS_TEAM);
                break;
        }
        if (!fullyOpen) {
            return;
        }
        getFontRenderer().drawShadow(matrixStack, localize("info.cofh.security"), sideOffset() + 18, 6, headerColor);
        getFontRenderer().drawShadow(matrixStack, localize("info.cofh.access") + ":", sideOffset() + 6, 66, subheaderColor);

        switch (mySecurable.getAccess()) {
            case PUBLIC:
                getFontRenderer().draw(matrixStack, localize("info.cofh.access_public"), sideOffset() + 14, 78, textColor);
                break;
            case PRIVATE:
                getFontRenderer().draw(matrixStack, localize("info.cofh.access_private"), sideOffset() + 14, 78, textColor);
                break;
            case FRIENDS:
                getFontRenderer().draw(matrixStack, localize("info.cofh.access_friends"), sideOffset() + 14, 78, textColor);
                break;
            case TEAM:
                getFontRenderer().draw(matrixStack, localize("info.cofh.access_team"), sideOffset() + 14, 78, textColor);
                break;
        }
        RenderHelper.resetShaderColor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (!myPlayer.equals(mySecurable.getOwner().getId())) {
            return true;
        }
        if (!fullyOpen) {
            return false;
        }
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        double x = mouseX - this.posX();
        double y = mouseY - this.posY();

        return !(x < 34) && !(x >= 78) && !(y < 18) && !(y >= 62);
    }

    @Override
    public void setFullyOpen() {

        if (!myPlayer.equals(mySecurable.getOwner().getId())) {
            return;
        }
        super.setFullyOpen();
    }

}

package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.CoreTextures;
import cofh.core.client.gui.IGuiAccess;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class EnchantPanel extends PanelScrolledText {

    public static int defaultSide = LEFT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0xf0f0f0;
    public static int defaultBackgroundColor = 0x6010bb;

    public EnchantPanel(IGuiAccess gui, String info) {

        this(gui, defaultSide, info);
    }

    protected EnchantPanel(IGuiAccess gui, int sideIn, String info) {

        super(gui, sideIn, info);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;
    }

    @Override
    public TextureAtlasSprite getIcon() {

        return CoreTextures.ICON_ENCHANTMENT;
    }

    @Override
    public Component getTitle() {

        return new TranslatableComponent("info.cofh.enchantments");
    }

}

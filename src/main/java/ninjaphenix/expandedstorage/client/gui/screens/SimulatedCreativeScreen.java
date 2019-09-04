package ninjaphenix.expandedstorage.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.CreativeButtonMover;

public class SimulatedCreativeScreen extends Screen
{
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tab_item_search.png");
    private static final Identifier TAB_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier BUTTON_TEXTURE = new Identifier("fabric", "textures/gui/creative_buttons.png");
    private static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private static int containerHeight = 136;
    private static int containerWidth = 195;
    private Screen returnTo;
    private int left;
    private int top;
    private int xpos;
    private int ypos;

    public SimulatedCreativeScreen(Screen parent)
    {
        super(new LiteralText(""));
        xpos = CreativeButtonMover.x;
        ypos = CreativeButtonMover.y;
        returnTo = parent;
    }

    @Override
    protected void init()
    {
        super.init();
        this.addButton(new ButtonWidget(width / 2 - 25, height / 2 + 100, 50, 20, "Save", (widget) -> onClose()));
    }

    @Override
    public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4)
    {
        xpos = (int) double_1 - left - 24;
        ypos = (int) double_2 - top - 11;
        return false;
    }

    @Override
    public void render(int int_1, int int_2, float float_1)
    {
        this.renderBackground();
        super.render(int_1, int_2, float_1);
        left = (this.width - containerWidth) / 2;
        top = (this.height - containerHeight) / 2;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
        for (int i = 1; i < 12; i++)
        {
            textureManager.bindTexture(TAB_TEXTURE);
            renderItemGroup(ItemGroup.GROUPS[i]);
        }
        textureManager.bindTexture(BACKGROUND_TEXTURE);
        this.blit(left, top, 0, 0, containerWidth, containerHeight);
        textureManager.bindTexture(TAB_TEXTURE);
        renderItemGroup(ItemGroup.GROUPS[0]);
        GuiLighting.disable();
        RenderSystem.disableAlphaTest();
        textureManager.bindTexture(BUTTON_TEXTURE);
        blit(left + xpos, top + ypos, 0, 0, 12, 12);
        blit(left + xpos + 10, top + ypos, 12, 0, 12, 12);
        font.drawWithShadow("NinjaPhenix's Fabric Creative Button Mover", left - 10, top - 40, 8453920);
    }

    protected void renderItemGroup(ItemGroup itemGroup_1)
    {
        int column = itemGroup_1.getColumn();
        int int_3 = 0;
        int int_4 = this.left + 28 * column;
        int int_5 = this.top;
        if (itemGroup_1.getIndex() == 0)
        {
            int_3 += 32;
        }

        if (itemGroup_1.isSpecial())
        {
            int_4 = this.left + containerWidth - 28 * (6 - column);
        }
        else if (column > 0)
        {
            int_4 += column;
        }

        if (itemGroup_1.isTopRow())
        {
            int_5 -= 28;
        }
        else
        {
            int_3 += 64;
            int_5 += containerHeight - 4;
        }

        RenderSystem.disableLighting();
        this.blit(int_4, int_5, column * 28, int_3, 28, 32);
    }


    @Override
    public void onClose()
    {
        CreativeButtonMover.x = xpos;
        CreativeButtonMover.y = ypos;
        CreativeButtonMover.saveValues();
        this.minecraft.openScreen(returnTo);
    }
}

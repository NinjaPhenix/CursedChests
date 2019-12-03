package ninjaphenix.expandedstorage.api.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.container.ExpandedContainer;

@Environment(EnvType.CLIENT)
public class BaseScreen extends AbstractContainerScreen<ExpandedContainer>
{
    private static final Identifier WIDGETS_TEXTURE = ExpandedStorage.getId("textures/gui/container/widgets.png");
    private double offsetX, offsetY;
    private int slotWidth, slotHeight;
    private boolean draggingWindow;

    public BaseScreen(ExpandedContainer container, PlayerInventory playerInventory, Text containerTitle)
    {
        super(container, playerInventory, containerTitle);
    }

    public static BaseScreen createScreen(ExpandedContainer container)
    {
        return new BaseScreen(container, MinecraftClient.getInstance().player.inventory, container.getName());
    }

    @Override
    public void init()
    {
        slotWidth = container.getWidth();
        slotHeight = container.getHeight();
        containerWidth = (slotWidth*18)+14;
        containerHeight = 18 + 14 + 7 + 4 + (18 * 3) + (18) + (18 * slotHeight);
        super.init();
    }

    @Override
    public void render(int x, int y, float delta)
    {
        renderBackground();
        drawBackground(delta, x, y);
        super.render(x, y, delta);
        drawMouseoverTooltip(x, y);
    }

    @Override
    protected void drawForeground(int x, int y)
    {
        font.draw(title.asFormattedString(), 8, 6, 4210752);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8, containerHeight - 94, 4210752);
    }

    @Override
    protected void drawBackground(float delta, int x, int y)
    {
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        blit(left, top, 22, 15, 7, 18);
        blit(left + 7, top, containerWidth - 14, 18, 28, 15, 1, 18, 256, 256);
        blit(left + containerWidth - 7, top, 7, 18, 47, 15, 7, 18, 256, 256);
        blit(left, top + 18, 7, slotHeight * 18 + 14, 22, 33, 7, 1, 256, 256);
        blit(left + containerWidth - 7, top + 18, 7, slotHeight * 18 + 14, 47, 33, 7, 1, 256, 256);
        blit(left + 7, top + 18 + (slotHeight * 18), containerWidth - 14, 14, 25, 19, 1, 14, 256, 256);

        for (int i = 0; i < container.getInventory().getInvSize(); i++)
        {
            Slot slot = container.slotList.get(i);
            blit(left + slot.xPosition - 1, top + slot.yPosition - 1, 18, 18, 29, 33, 18, 18, 256, 256);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double x, double y, int left, int top, int mouseButton)
    {
        boolean left_up_down = x < left || y < top || y > top + height;
        boolean right = x > left + width;
        return left_up_down || right;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == 0 && mouseX > left && mouseX < left + containerWidth && mouseY > top && mouseY <= top + 30)
        {
            draggingWindow = true;
            offsetX = mouseX - left;
            offsetY = mouseY - top;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (draggingWindow)
        {
            left = (int) (mouseX - offsetX);
            if (left < 0) left = 0;
            else if (left + containerWidth > width) left = width - containerWidth;
            top = (int) (mouseY - offsetY);
            if (top < 0) top = 0;
            else if (top + containerHeight > height) top = height - containerHeight;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (draggingWindow)
        {
            draggingWindow = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}


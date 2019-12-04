package ninjaphenix.expandedstorage.api.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
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
    private final int invSize;

    public BaseScreen(ExpandedContainer container, PlayerInventory playerInventory, Text containerTitle)
    {
        super(container, playerInventory, containerTitle);
        invSize = container.getInventory().getInvSize();
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
    public void render(int mouseX, int mouseY, float delta)
    {
        renderBackground();
        super.render(mouseX, mouseY, delta);
        drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int x, int y)
    {
        font.draw(title.asFormattedString(), 8, 6, 4210752);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8, containerHeight - 94, 4210752);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        blit(x, y, 22, 15, 7, 18); // top left
        blit(x + 7, y, containerWidth - 14, 18, 28, 15, 1, 18, 256, 256); // top line
        blit(x + containerWidth - 7, y, 7, 18, 47, 15, 7, 18, 256, 256); // top right
        blit(x, y + 18, 7, slotHeight * 18 + 14, 22, 33, 7, 1, 256, 256); // left side
        blit(x + containerWidth - 7, y + 18, 7, slotHeight * 18 + 14, 47, 33, 7, 1, 256, 256); // right side
        blit(x + 7, y + 18 + (slotHeight * 18), containerWidth - 14, 14, 25, 19, 1, 14, 256, 256); // chest-player spacer
        blit(x, y + 18 + 14 + (slotHeight * 18), 25, 7, 22, 51, 25, 7, 256, 256); // bottom left
        blit(x + containerWidth - 25, y + 18 + 14 + (slotHeight * 18), 25, 7, 29, 51, 25, 7, 256, 256); // bottom right
        blit(x + 7, y + 18 + 14 + (slotHeight * 18), containerWidth - 14, 7, 25, 51, 1, 7, 256, 256); // bottom line
        blit(x + 7, y + 18, slotWidth * 18, slotHeight * 18, 26, 18, 1, 1, 256, 256); // chest slots background
        for (int i = 0; i < invSize; i++)
        {
            Slot slot = container.slotList.get(i);
            blit(x + slot.xPosition - 1, y + slot.yPosition - 1, 18, 18, 29, 33, 18, 18, 256, 256);
        }
        blit(x+container.slotList.get(invSize).xPosition-8, y + 32 + slotHeight*18, 176, 83, 22, 58, 176, 83, 256, 256); // player inventory
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
        if (button == 0 && mouseX > x && mouseX < x + containerWidth && mouseY > y && mouseY <= y + 18)
        {
            draggingWindow = true;
            offsetX = mouseX - x;
            offsetY = mouseY - y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (draggingWindow)
        {
            x = (int) (mouseX - offsetX);
            if (x < 0) x = 0;
            else if (x + containerWidth > width) x = width - containerWidth;
            y = (int) (mouseY - offsetY);
            if (y < 0) y = 0;
            else if (y + containerHeight > height) y = height - containerHeight;
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


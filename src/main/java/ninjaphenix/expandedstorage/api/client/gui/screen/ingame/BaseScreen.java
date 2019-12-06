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
    private final int chestPlayerWidthCompare;

    public BaseScreen(ExpandedContainer container, PlayerInventory playerInventory, Text containerTitle)
    {
        super(container, playerInventory, containerTitle);
        invSize = container.getInventory().getInvSize();
        chestPlayerWidthCompare = Integer.compare(container.getWidth(), 9);
    }

    public static BaseScreen createScreen(ExpandedContainer container)
    {
        return new BaseScreen(container, MinecraftClient.getInstance().player.inventory, container.getName());
    }

    @Override
    public void init()
    {
        slotWidth = container.getWidth() * 18;
        slotHeight = container.getHeight() * 18;
        containerWidth = chestPlayerWidthCompare == -1 ? 176 : slotWidth + 14;
        containerHeight = 18 + 14 + 7 + 4 + (18 * 3) + (18) + slotHeight;
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
    protected void drawForeground(int mouseX, int mouseY)
    {
        final int chestLeft = container.slotList.get(0).xPosition;
        final int inventoryLeft = container.slotList.get(invSize).xPosition;
        font.draw(title.asFormattedString(), chestLeft, 6, 4210752);
        font.draw(playerInventory.getDisplayName().asFormattedString(), inventoryLeft, containerHeight - 94, 4210752);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        final int chestLeft = x + container.slotList.get(0).xPosition - 8;
        final int chestTop = y + container.slotList.get(0).yPosition - 18;
        blit(chestLeft, chestTop, 22, 15, 7, 18); // top left
        blit(chestLeft + 7, chestTop, slotWidth, 18, 28, 15, 1, 18, 256, 256); // top line
        blit(chestLeft + slotWidth + 7, chestTop, 7, 18, 47, 15, 7, 18, 256, 256); // top right
        blit(chestLeft + 7, chestTop + 18, slotWidth, slotHeight, 26, 18, 1, 1, 256, 256); // chest slots background
        for (int i = 0; i < invSize; i++)
        {
            Slot slot = container.slotList.get(i);
            blit(x + slot.xPosition - 1, y + slot.yPosition - 1, 18, 18, 29, 33, 18, 18, 256, 256);
        }
        final int inventoryLeft = x + container.slotList.get(invSize).xPosition-8;
        final int inventoryTop = y + container.slotList.get(invSize).yPosition;
        switch(chestPlayerWidthCompare)
        {
            case 0:
                blit(chestLeft, chestTop + 18, 7, slotHeight + 14, 22, 33, 7, 1, 256, 256); // left side
                blit(chestLeft + slotWidth + 7, chestTop + 18, 7, slotHeight  + 14, 47, 33, 7, 1, 256, 256); // right side
                blit(chestLeft + 7, chestTop + 18 + slotHeight, slotWidth, 14, 25, 19, 1, 14, 256, 256); // chest-plachestToper spacer
                blit(inventoryLeft, inventoryTop - 1, 176, 83, 22, 58, 176, 83, 256, 256); // player inventory
                break;
            case -1:
                blit(chestLeft, chestTop + 18, 7, slotHeight + 14, 22, 33, 7, 1, 256, 256); // left side
                blit(chestLeft + slotWidth + 7, chestTop + 18, 7, slotHeight  + 14, 47, 33, 7, 1, 256, 256); // right side
                blit(chestLeft + 7, chestTop + 18 + slotHeight, slotWidth, 14, 25, 19, 1, 14, 256, 256); // chest-plachestToper spacer
                blit(inventoryLeft, inventoryTop - 18, 7, 18, 22, 15, 7, 18, 256, 256); // player inv top left corner
                blit(inventoryLeft + 7, inventoryTop - 18, chestLeft - inventoryLeft, 18, 26, 15, 1, 18, 256, 256); // player inv top left
                blit(inventoryLeft + 9 * 18 + 7, inventoryTop - 18, 7, 18, 47, 15, 7, 18, 256, 256); // player inv top right corner
                blit(chestLeft + 7 + slotWidth, inventoryTop - 18, chestLeft - inventoryLeft, 18, 26, 15, 1, 18, 256, 256); // player inv top right
                blit(inventoryLeft, inventoryTop - 1, 176, 83, 22, 58, 176, 83, 256, 256); // player inventory
                break;
            case 1:
                blit(chestLeft, chestTop + 18, 7, slotHeight + 7, 22, 33, 7, 1, 256, 256); // left side
                blit(chestLeft + slotWidth + 7, chestTop + 18, 7, slotHeight  + 7, 47, 33, 7, 1, 256, 256); // right side
                blit(chestLeft + 7, chestTop + 18 + slotHeight, slotWidth, 7, 25, 19, 1, 14, 256, 256); // chest-plachestToper spacer
                blit(x, y + 11 + 14 + slotHeight, 25, 7, 22, 51, 25, 7, 256, 256); // bottom left
                blit(x + containerWidth - 25, y + 11 + 14 + slotHeight, 25, 7, 29, 51, 25, 7, 256, 256); // bottom right
                blit(x + 7, y + 11 + 14 + slotHeight, containerWidth - 14, 7, 25, 51, 1, 7, 256, 256); // bottom line
                blit(inventoryLeft + 7, chestTop + 25 + slotHeight, 162, 7, 25, 19, 1, 14, 256, 256); // chest-plachestToper spacer
                blit(inventoryLeft, inventoryTop - 1, 176, 83, 22, 58, 176, 83, 256, 256); // player inventory
                blit(inventoryLeft, inventoryTop - 8, 7, 7, 54, 25, 7, 7, 256, 256); // spacer-player left corner
                blit(inventoryLeft + 169, inventoryTop - 8, 7, 7, 61, 25, 7, 7, 256, 256); // spacer-player right corner
                // break;
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
        if (button == 0 && mouseX > x && mouseX < x + slotWidth + 14 && mouseY > y && mouseY <= y + 18)
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
            if (y < -1) y = -1;
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


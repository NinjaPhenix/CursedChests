package ninjaphenix.expandedstorage.api.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.client.gui.widget.SearchTextFieldWidget;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;

@OnlyIn(Dist.CLIENT)
public class ScrollableScreen extends ContainerScreen<ScrollableContainer>
{
	private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private static final ResourceLocation WIDGETS_TEXTURE = ExpandedStorage.getRl("textures/gui/container/widgets.png");
	private final int displayedRows;
	private final int totalRows;
	private int topRow;
	private double progress;
	private boolean dragging;
	private SearchTextFieldWidget searchBox;
	private String searchBoxOldText;

	public ScrollableScreen(ScrollableContainer container, PlayerInventory playerInventory, ITextComponent containerTitle)
	{
		super(container, playerInventory, containerTitle);
		totalRows = container.getRows();
		topRow = 0;
		displayedRows = hasScrollbar() ? 6 : totalRows;
		// todo: change for JEI if (hasScrollbar() && !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) containerWidth += 22;
		ySize = 114 + displayedRows * 18;
		progress = 0;
		container.setSearchTerm("");
		searchBoxOldText = "";
	}

	@Override
	protected void init()
	{
		super.init();
		searchBox = addButton(new SearchTextFieldWidget(font, guiLeft + 82, guiTop + 127, 80, 8, ""));
		searchBox.setMaxStringLength(50);
		searchBox.setEnableBackgroundDrawing(false);
		searchBox.setVisible(hasScrollbar());
		searchBox.setTextColor(16777215);
		searchBox.setResponder(str ->
		{
			if (str.equals(searchBoxOldText)) return;
			container.setSearchTerm(str);
			progress = 0;
			topRow = 0;
			searchBoxOldText = str;
		});
	}

	@Override
	public void tick() { searchBox.tick(); }

	@Override
	public void render(int mouseX, int mouseY, float lastFrameDuration)
	{
		renderBackground();
		drawGuiContainerBackgroundLayer(lastFrameDuration, mouseX, mouseY);
		super.render(mouseX, mouseY, lastFrameDuration);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		font.drawString(title.getFormattedText(), 8, 6, 4210752);
		font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 94, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float lastFrameDuration, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1, 1, 1, 1);
		minecraft.getTextureManager().bindTexture(BASE_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		blit(x, y, 0, 0, xSize, displayedRows * 18 + 17);
		blit(x, y + displayedRows * 18 + 17, 0, 126, xSize, 96);
		if (hasScrollbar())
		{
			minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
			blit(x + 172, y, 0, 0, 22, 132);
			blit(x + 174, (int) (y + 18 + 91 * progress), 22, 0, 12, 15);
			blit(x + 79, y + 126, 34, 0, 90, 11);
			searchBox.render(mouseX, mouseY, lastFrameDuration);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta)
	{
		if (hasScrollbar())
		{
			setTopRow(topRow - (int) scrollDelta);
			progress = ((double) topRow) / ((double) (totalRows - 6));
			return true;
		}
		return false;
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int mouseButton)
	{
		boolean left_up_down = mouseX < left || mouseY < top || mouseY > top + height;
		boolean right = mouseX > left + width;
		if (hasScrollbar()) right = (right && mouseY > top + 132) || mouseX > left + width + 18;
		return left_up_down || right;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (!dragging) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		progress = MathHelper.clamp((mouseY - guiTop - 25.5) / 90, 0, 1);
		setTopRow((int) (progress * (totalRows - 6)));
		return true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (searchBox.isFocused() && !searchBox.mouseInBounds(mouseX, mouseY) && button == 0)
		{
			searchBox.changeFocus(true);
			this.setFocused(null);
		}
		if (button == 0 && guiLeft + 172 < mouseX && mouseX < guiLeft + 184 && guiTop + 18 < mouseY && mouseY < guiTop + 123)
		{
			dragging = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (dragging && button == 0) dragging = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private void setTopRow(int row)
	{
		topRow = MathHelper.clamp(row, 0, totalRows - 6);
		container.updateSlotPositions(topRow, false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (keyCode == 256)
		{
			minecraft.player.closeScreen();
			return true;
		}
		if (!searchBox.isFocused())
		{
			if (minecraft.gameSettings.keyBindChat.matchesKey(keyCode, scanCode))
			{
				searchBox.changeFocus(true);
				this.setFocused(searchBox);
				searchBox.ignoreNextChar();
				return true;
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		return searchBox.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char character, int int_1)
	{
		if (searchBox.isFocused()) return searchBox.charTyped(character, int_1);
		return super.charTyped(character, int_1);
	}

	@Override
	public void resize(Minecraft client, int width, int height)
	{
		String text = searchBox.getText();
		boolean focused = searchBox.isFocused();
		super.resize(client, width, height);
		searchBox.setText(text);
		if (focused)
		{
			searchBox.changeFocus(true);
			setFocused(searchBox);
		}
	}

	public int getTop() { return this.guiTop; }

	public int getLeft() { return this.guiLeft; }

	public boolean hasScrollbar() { return totalRows > 6; }
}


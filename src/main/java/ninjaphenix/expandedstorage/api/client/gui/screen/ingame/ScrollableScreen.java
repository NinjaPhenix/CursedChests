package ninjaphenix.expandedstorage.api.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.client.gui.widget.SearchTextFieldWidget;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;

@Environment(EnvType.CLIENT)
public class ScrollableScreen extends AbstractContainerScreen<ScrollableContainer> implements ContainerProvider<ScrollableContainer>
{
	private static final Identifier BASE_TEXTURE = new Identifier("textures/gui/container/generic_54.png");
	private static final Identifier WIDGETS_TEXTURE = ExpandedStorage.getId("textures/gui/container/widgets.png");
	private final int displayedRows;
	private final int totalRows;
	private int topRow;
	private double progress;
	private boolean dragging;
	private SearchTextFieldWidget searchBox;
	private String searchBoxOldText;

	public ScrollableScreen(ScrollableContainer container, PlayerInventory playerInventory, Text containerTitle)
	{
		super(container, playerInventory, containerTitle);
		totalRows = container.getRows();
		topRow = 0;
		displayedRows = hasScrollbar() ? 6 : totalRows;
		if (hasScrollbar() && !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) { containerWidth += 22; }
		containerHeight = 114 + displayedRows * 18;
		progress = 0;
		container.setSearchTerm("");
		searchBoxOldText = "";
	}

	public static ScrollableScreen createScreen(ScrollableContainer container)
	{
		return new ScrollableScreen(container, MinecraftClient.getInstance().player.inventory, container.getDisplayName());
	}

	@Override
	public void init()
	{
		super.init();
		searchBox = addButton(new SearchTextFieldWidget(font, x + 82, y + 127, 80, 8, ""));
		searchBox.setMaxLength(50);
		searchBox.setHasBorder(false);
		searchBox.setVisible(hasScrollbar());
		searchBox.setEditableColor(16777215);
		searchBox.setChangedListener(str ->
		{
			if (str.equals(searchBoxOldText)) { return; }
			container.setSearchTerm(str);
			progress = 0;
			topRow = 0;
			searchBoxOldText = str;
		});
	}

	@Override
	public void tick() { searchBox.tick(); }

	@Override
	public void render(int mouseX, int mouseY, float delta)
	{
		renderBackground();
		drawBackground(delta, mouseX, mouseY);
		super.render(mouseX, mouseY, delta);
		drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY)
	{
		font.draw(title.asFormattedString(), 8, 6, 4210752);
		font.draw(playerInventory.getDisplayName().asFormattedString(), 8, containerHeight - 94, 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1, 1, 1, 1);
		minecraft.getTextureManager().bindTexture(BASE_TEXTURE);
		int left = (width - containerWidth) / 2;
		int top = (height - containerHeight) / 2;
		blit(left, top, 0, 0, containerWidth, displayedRows * 18 + 17);
		blit(left, top + displayedRows * 18 + 17, 0, 126, containerWidth, 96);
		if (hasScrollbar())
		{
			minecraft.getTextureManager().bindTexture(WIDGETS_TEXTURE);
			blit(left + 172, top, 0, 0, 22, 132);
			blit(left + 174, (int) (top + 18 + 91 * progress), 22, 0, 12, 15);
			blit(left + 79, top + 126, 34, 0, 90, 11);
			searchBox.render(mouseX, mouseY, delta);
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
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int mouseButton)
	{
		boolean left_up_down = mouseX < left || mouseY < top || mouseY > top + height;
		boolean right = mouseX > left + width;
		if (hasScrollbar()) { right = (right && mouseY > top + 132) || mouseX > left + width + 18; }
		return left_up_down || right;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (!dragging) { return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY); }
		progress = MathHelper.clamp((mouseY - y - 25.5) / 90, 0, 1);
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
		if (button == 0 && x + 172 < mouseX && mouseX < x + 184 && y + 18 < mouseY && mouseY < y + 123)
		{
			dragging = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (dragging && button == 0) { dragging = false; }
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
			minecraft.player.closeContainer();
			return true;
		}
		if (!searchBox.isFocused())
		{
			if (minecraft.options.keyChat.matchesKey(keyCode, scanCode))
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
	public boolean charTyped(char character, int keyCode)
	{
		if (searchBox.isFocused()) { return searchBox.charTyped(character, keyCode); }
		return super.charTyped(character, keyCode);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height)
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

	public int getTop() { return this.y; }

	public int getLeft() { return this.x; }

	public boolean hasScrollbar() { return totalRows > 6; }
}


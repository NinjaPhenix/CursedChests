package ninjaphenix.expandedstorage.api.client.gui.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTextFieldWidget extends TextFieldWidget
{
	private boolean ignoreNextChar;

	public SearchTextFieldWidget(final FontRenderer fontRenderer, final int x, final int y, final int width, final int height, final String message)
	{
		super(fontRenderer, x, y, width, height, message);
		ignoreNextChar = false;
	}

	@Override
	public boolean mouseClicked(final double x, final double y, final int button)
	{
		if (getVisible() && button == 1 && clicked(x, y))
		{
			setText("");
			return true;
		}
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean charTyped(final char character, final int i)
	{
		if (ignoreNextChar)
		{
			ignoreNextChar = false;
			return false;
		}
		return super.charTyped(character, i);
	}

	public boolean mouseInBounds(final double x, final double y) { return clicked(x, y); }

	public void ignoreNextChar() { ignoreNextChar = true; }
}
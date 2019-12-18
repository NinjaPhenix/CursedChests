package ninjaphenix.expandedstorage.api.client.gui.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTextFieldWidget extends TextFieldWidget
{
	private boolean ignoreNextChar;

	public SearchTextFieldWidget(FontRenderer fontRenderer, int x, int y, int width, int height, String message)
	{
		super(fontRenderer, x, y, width, height, message);
		ignoreNextChar = false;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button)
	{
		if (getVisible() && button == 1 && clicked(x, y))
		{
			setText("");
			return true;
		}
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean charTyped(char character, int int_1)
	{
		if (ignoreNextChar)
		{
			ignoreNextChar = false;
			return false;
		}
		return super.charTyped(character, int_1);
	}

	public boolean mouseInBounds(double x, double y) { return clicked(x, y); }

	public void ignoreNextChar() { ignoreNextChar = true; }
}
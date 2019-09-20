package ninjaphenix.cursedchests.api.client.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class SearchTextFieldWidget extends TextFieldWidget
{

	private boolean ignoreNextChar;

	public SearchTextFieldWidget(TextRenderer textRenderer_1, int int_1, int int_2, int int_3, int int_4, String string_1)
	{
		super(textRenderer_1, int_1, int_2, int_3, int_4, string_1);
		ignoreNextChar = false;
	}

	@Override public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (isVisible() && button == 1 && clicked(mouseX, mouseY))
		{
			this.setText("");
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override public boolean charTyped(char char_1, int int_1)
	{
		if (ignoreNextChar)
		{
			ignoreNextChar = false;
			return false;
		}
		return super.charTyped(char_1, int_1);
	}

	public boolean mouseInBounds(double mouseX, double mouseY)
	{
		return this.clicked(mouseX, mouseY);
	}

	public void ignoreNextChar()
	{
		ignoreNextChar = true;
	}
}
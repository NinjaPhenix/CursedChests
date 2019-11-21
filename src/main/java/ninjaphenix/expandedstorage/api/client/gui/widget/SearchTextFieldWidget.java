package ninjaphenix.expandedstorage.api.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Environment(EnvType.CLIENT)
public class SearchTextFieldWidget extends TextFieldWidget
{
    private boolean ignoreNextChar;

    public SearchTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, String message)
    {
        super(textRenderer, x, y, width, height, message);
        ignoreNextChar = false;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button)
    {
        if (isVisible() && button == 1 && clicked(x, y))
        {
            setText("");
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean charTyped(char character, int keyCode)
    {
        if (ignoreNextChar)
        {
            ignoreNextChar = false;
            return false;
        }
        return super.charTyped(character, keyCode);
    }

    public boolean mouseInBounds(double x, double y) { return clicked(x, y); }

    public void ignoreNextChar() { ignoreNextChar = true; }
}
package ninjaphenix.cursedchests.client;

import ninjaphenix.cursedchests.api.client.gui.container.ScrollableScreen;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.REIPluginEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

public class CursedChestREIPlugin implements REIPluginEntry
{
    @Override
    public void registerBounds(DisplayHelper displayHelper)
    {
        BaseBoundsHandler handler = displayHelper.getBaseBoundsHandler();
        handler.registerExclusionZones(ScrollableScreen.class, isOnRightSide ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            ScrollableScreen screen = (ScrollableScreen) client.currentScreen;
            ArrayList<Rectangle> rv = new ArrayList<>(1);
            if (isOnRightSide && screen.hasScrollbar()) { rv.add(new Rectangle(screen.getLeft() + 172, screen.getTop(), 22, 132));}
            return rv;
        });
    }

    @Override
    public Identifier getPluginIdentifier() { return new Identifier("cursedchests", "reiplugin"); }
}

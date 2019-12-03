package ninjaphenix.expandedstorage.client;

import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;

@Environment(EnvType.CLIENT)
public class ExpandedStorageREIPlugin implements REIPluginV0
{
    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException { return SemanticVersion.parse("3.0-pre"); }

    @Override
    public Identifier getPluginIdentifier() { return ExpandedStorage.getId("reiplugin"); }

    @Override
    public void registerBounds(DisplayHelper displayHelper)
    {
        //displayHelper.getBaseBoundsHandler().registerExclusionZones(ScrollableScreen.class, isOnRightSide ->
        //{
        //    ScrollableScreen screen = (ScrollableScreen) MinecraftClient.getInstance().currentScreen;
        //    ArrayList<Rectangle> rv = new ArrayList<>(1);
        //    if (isOnRightSide && screen.hasScrollbar()) rv.add(new Rectangle(screen.getLeft() + 172, screen.getTop(), 22, 132));
        //    return rv;
        //});
    }
}

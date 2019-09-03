package ninjaphenix.expandedstorage.client;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import ninjaphenix.expandedstorage.client.gui.SimulatedCreativeScreen;

import java.util.function.Function;

public class CreativeButtonMoverModMenuPlugin implements ModMenuApi
{
    @Override
    public String getModId() { return "fabric-item-groups-v0"; }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() { return SimulatedCreativeScreen::new; }
}

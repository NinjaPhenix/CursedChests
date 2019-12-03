package ninjaphenix.expandedstorage.api.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import ninjaphenix.expandedstorage.api.container.ExpandedContainer;

@Environment(EnvType.CLIENT)
public class PagedScreen extends AbstractContainerScreen<ExpandedContainer> implements ContainerProvider<ExpandedContainer>
{
    public PagedScreen(ExpandedContainer container, PlayerInventory playerInventory, Text name)
    {
        super(container, playerInventory, name);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY)
    {

    }
}

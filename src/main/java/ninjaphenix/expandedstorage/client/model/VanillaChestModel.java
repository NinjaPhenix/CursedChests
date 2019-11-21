package ninjaphenix.expandedstorage.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VanillaChestModel extends SingleChestModel
{
    public VanillaChestModel()
    {
        super(96, 48);
        lid.addCuboid(0, 0, 0, 30, 5, 14, 0);
        lid.addCuboid(14, -2, 14, 2, 4, 1, 0);
        lid.setPivot(1, 9, 1);
        base.addCuboid(0, 0, 0, 30, 10, 14, 0);
        base.setPivot(1, 0, 1);
    }
}
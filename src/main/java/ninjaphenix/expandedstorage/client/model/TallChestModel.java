package ninjaphenix.expandedstorage.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TallChestModel extends SingleChestModel
{
    public TallChestModel()
    {
        super(64, 64);
        lid.addCuboid(0, 0, 0, 14, 5, 14, 0);
        lid.addCuboid(6, -1, 14, 2, 4, 1, 0);
        lid.setPivot(1, 25, 1);
        base.addCuboid(0, 0, 0, 14, 26, 14, 0);
        base.setPivot(1, 0, 1);
    }
}
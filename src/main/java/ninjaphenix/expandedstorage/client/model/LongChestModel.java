package ninjaphenix.expandedstorage.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LongChestModel extends SingleChestModel
{
    public LongChestModel()
    {
        super(96, 80);
        lid.addCuboid(0, -5, -30, 14, 5, 30, 0);
        lid.addCuboid(6, -2, -31, 2, 4, 1, 0);
        lid.setRotationPoint(1, 7, 31);
        base.setTextureOffset(0, 35);
        base.addCuboid(0, 0, 0, 14, 10, 30, 0);
        base.setRotationPoint(1, 6, 1);
    }
}
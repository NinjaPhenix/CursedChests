package ninjaphenix.expandedstorage.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TallChestEntityModel extends SingleChestEntityModel
{
    public TallChestEntityModel()
    {
        super(64, 64);
        lid.addCuboid(0, -5, -14, 14, 5, 14, 0);
        lid.addCuboid(6, -2, -15, 2, 4, 1, 0);
        lid.setRotationPoint(1, 7, 15);
        lid.y -= 1;
        base.addCuboid(0, -16, 0, 14, 26, 14, 0);
        base.setRotationPoint(1, 6, 1);
    }
}
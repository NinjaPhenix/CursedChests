package ninjaphenix.cursedchests.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TallChestEntityModel extends SingleChestEntityModel
{
    public TallChestEntityModel()
    {
        super(64, 64);
        lid.addBox(0, -5, -14, 14, 5, 14, 0);
        lid.setRotationPoint(1, 7, 15);
        lid.y -= 1;
        hatch.addBox(-1, -2, -15, 2, 4, 1, 0);
        hatch.setRotationPoint(8, 7, 15);
        hatch.y -= 1;
        base.addBox(0, -16, 0, 14, 26, 14, 0);
        base.setRotationPoint(1, 6, 1);
    }
}
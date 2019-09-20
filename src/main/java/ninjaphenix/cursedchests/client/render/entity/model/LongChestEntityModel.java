package ninjaphenix.cursedchests.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.ChestEntityModel;

@Environment(EnvType.CLIENT)
public class LongChestEntityModel extends ChestEntityModel
{
    public LongChestEntityModel()
    {
        lid = new Cuboid(this, 0, 0).setTextureSize(96, 96);
        lid.addBox(0, -5, -30, 14, 5, 30, 0);
        lid.rotationPointX = 1;
        lid.rotationPointY = 7;
        lid.rotationPointZ = 31;
        hatch = new Cuboid(this, 0, 0).setTextureSize(96, 96);
        hatch.addBox(-1, -2, -31, 2, 4, 1, 0);
        hatch.rotationPointX = 8;
        hatch.rotationPointY = 7;
        hatch.rotationPointZ = 31;
        base = new Cuboid(this, 0, 35).setTextureSize(96, 96);
        base.addBox(0, 0, 0, 14, 10, 30, 0);
        base.rotationPointX = 1;
        base.rotationPointY = 6;
        base.rotationPointZ = 1;
    }
}

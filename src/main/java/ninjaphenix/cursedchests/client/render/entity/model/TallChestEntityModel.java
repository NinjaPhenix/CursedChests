package ninjaphenix.cursedchests.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.ChestEntityModel;

@Environment(EnvType.CLIENT)
public class TallChestEntityModel extends ChestEntityModel
{
    public TallChestEntityModel()
    {
        lid = new Cuboid(this, 0, 0).setTextureSize(64, 128);
        lid.addBox(0, -5, -14, 14, 5, 14, 0);
        lid.rotationPointX = 1;
        lid.rotationPointY = 7;
        lid.rotationPointZ = 15;
        lid.y -= 1;
        hatch = new Cuboid(this, 0, 0).setTextureSize(64, 128);
        hatch.addBox(-1, -2, -15, 2, 4, 1, 0);
        hatch.rotationPointX = 8;
        hatch.rotationPointY = 7;
        hatch.rotationPointZ = 15;
        hatch.y -= 1;
        base = new Cuboid(this, 0, 19).setTextureSize(64, 128);
        base.addBox(0, -16, 0, 14, 26, 14, 0);
        base.rotationPointX = 1;
        base.rotationPointY = 6;
        base.rotationPointZ = 1;
    }
}

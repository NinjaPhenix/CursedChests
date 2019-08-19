package ninjaphenix.cursedchests.client.render.entity.model.old;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.ChestEntityModel;

public class OldVanillaChestEntityModel extends ChestEntityModel
{
    public OldVanillaChestEntityModel()
    {
        lid = null;
        hatch = null;
        base = new Cuboid(this, 0, 0).setTextureSize(96, 32);
        base.addBox(0, 0, 0, 32, 16, 16, 0);
    }

    @Override
    public void method_2799() { base.render(0.0625F); }
}

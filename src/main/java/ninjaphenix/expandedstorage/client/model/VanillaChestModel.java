package ninjaphenix.expandedstorage.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VanillaChestModel extends SingleChestModel
{
    public VanillaChestModel()
    {
        super(96, 48);
        lid.addBox(0, -5, -14, 30, 5, 14, 0);
        lid.addBox(14, -2, -15, 2, 4, 1, 0);
        lid.setRotationPoint(1, 7, 15);
        base.addBox(0, 0, 0, 30, 10, 14, 0);
        base.setRotationPoint(1, 6, 1);
    }
}
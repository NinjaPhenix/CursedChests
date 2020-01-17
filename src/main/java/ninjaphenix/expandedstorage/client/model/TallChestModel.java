package ninjaphenix.expandedstorage.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TallChestModel extends SingleChestModel
{
	public TallChestModel()
	{
		super(64, 64);
		lid.addBox(0, -5, -14, 14, 5, 14, 0);
		lid.addBox(6, -2, -15, 2, 4, 1, 0);
		lid.setRotationPoint(1, 7, 15);
		lid.offsetY -= 1;
		base.addBox(0, -16, 0, 14, 26, 14, 0);
		base.setRotationPoint(1, 6, 1);
	}
}
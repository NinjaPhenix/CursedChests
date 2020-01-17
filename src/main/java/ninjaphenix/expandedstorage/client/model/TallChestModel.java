package ninjaphenix.expandedstorage.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TallChestModel extends SingleChestModel
{
	public TallChestModel()
	{
		super(64, 64);
		lid.func_228301_a_(0, 0, 0, 14, 5, 14, 0);
		lid.func_228301_a_(6, -2, 14, 2, 4, 1, 0);
		lid.setRotationPoint(1, 25, 1);
		base.func_228301_a_(0, 0, 0, 14, 26, 14, 0);
		base.setRotationPoint(1, 0, 1);
	}
}
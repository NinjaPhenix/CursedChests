package ninjaphenix.expandedstorage.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VanillaChestModel extends SingleChestModel
{
	public VanillaChestModel()
	{
		super(96, 48);
		lid.func_228301_a_(0, 0, 0, 30, 5, 14, 0);
		lid.func_228301_a_(14, -2, 14, 2, 4, 1, 0);
		lid.setRotationPoint(1, 9, 1);
		base.func_228301_a_(0, 0, 0, 30, 10, 14, 0);
		base.setRotationPoint(1, 0, 1);
	}
}
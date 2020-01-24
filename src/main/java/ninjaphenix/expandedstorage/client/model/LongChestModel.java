package ninjaphenix.expandedstorage.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LongChestModel extends SingleChestModel
{
	public LongChestModel()
	{
		super(96, 80);
		lid.func_228301_a_(0, 0, 0, 14, 5, 30, 0);
		lid.func_228301_a_(6, -2, 30, 2, 4, 1, 0);
		lid.setRotationPoint(1, 9, -15);
		base.func_78784_a(0, 35);
		base.func_228301_a_(0, 0, 0, 14, 10, 30, 0);
		base.setRotationPoint(1, 0, -15);
	}
}
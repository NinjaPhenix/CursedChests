package ninjaphenix.expandedstorage.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LongChestModel extends SingleChestModel
{
	public LongChestModel()
	{
		super(96, 80);
		lid.addBox(0, 0, 0, 14, 5, 30, 0);
		lid.addBox(6, -2, 30, 2, 4, 1, 0);
		lid.setRotationPoint(1, 9, -15);
		base.setTextureOffset(0, 35);
		base.addBox(0, 0, 0, 14, 10, 30, 0);
		base.setRotationPoint(1, 0, -15);
	}
}
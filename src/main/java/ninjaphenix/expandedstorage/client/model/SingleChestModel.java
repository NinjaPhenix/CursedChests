package ninjaphenix.expandedstorage.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SingleChestModel extends Model
{
	protected RendererModel lid;
	protected RendererModel base;

	public SingleChestModel(int textureWidth, int textureHeight)
	{
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		lid = new RendererModel(this, 0, 0);
		base = new RendererModel(this, 0, 19);
	}

	public SingleChestModel()
	{
		this(64, 48);
		lid.addBox(0, -5, -14, 14, 5, 14, 0);
		lid.addBox(6, -2, -15, 2, 4, 1, 0);
		lid.setRotationPoint(1, 7, 15);
		base.addBox(0, 0, 0, 14, 10, 14, 0);
		base.setRotationPoint(1, 6, 1);
	}

	public void render()
	{
		lid.render(0.0625F);
		base.render(0.0625F);
	}

	public void setLidPitch(float pitch) { lid.rotateAngleX = pitch; }
}
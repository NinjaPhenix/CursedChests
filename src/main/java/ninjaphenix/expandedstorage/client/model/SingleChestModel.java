package ninjaphenix.expandedstorage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SingleChestModel extends Model
{
	protected ModelRenderer lid;
	protected ModelRenderer base;

	public SingleChestModel(int textureWidth, int textureHeight)
	{
		super(RenderType::func_228638_b_);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		lid = new ModelRenderer(this, 0, 0);
		base = new ModelRenderer(this, 0, 19);
	}

	public SingleChestModel()
	{
		this(64, 48);
		lid.func_228301_a_(0, -5, -14, 14, 5, 14, 0);
		lid.func_228301_a_(6, -2, -15, 2, 4, 1, 0);
		lid.setRotationPoint(1, 7, 15);
		base.func_228301_a_(0, 0, 0, 14, 10, 14, 0);
		base.setRotationPoint(1, 6, 1);
	}

	public void setLidPitch(float pitch)
	{
		pitch = 1.0f - pitch;
		lid.rotateAngleX = -((1.0F - pitch * pitch * pitch) * 1.5707964F);
	}

	public void render(MatrixStack stack, IVertexBuilder builder, int i, int j)
	{
		func_225598_a_(stack, builder, i, j, 1, 1, 1, 1);
	}

	@Override
	public void func_225598_a_(MatrixStack stack, IVertexBuilder builder, int i, int j, float r, float g, float b, float f)
	{
		base.func_228308_a_(stack, builder, i, j);
		lid.func_228308_a_(stack, builder, i, j);
	}
}
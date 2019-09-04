package ninjaphenix.expandedstorage.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class SingleChestModel extends Model
{
    protected ModelPart lid;
    protected ModelPart base;

    public SingleChestModel(int textureWidth, int textureHeight)
    {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        lid = new ModelPart(this, 0, 0);
        base = new ModelPart(this, 0, 19);
    }

    public SingleChestModel()
    {
        this(64, 48);
        lid.addCuboid(0, -5, -14, 14, 5, 14, 0);
        lid.addCuboid(6, -2, -15, 2, 4, 1, 0);
        lid.setRotationPoint(1, 7, 15);
        base.addCuboid(0, 0, 0, 14, 10, 14, 0);
        base.setRotationPoint(1, 6, 1);
    }

    public void render()
    {
        lid.render(0.0625F);
        base.render(0.0625F);
    }

    public void setLidPitch(float pitch) { lid.pitch = pitch; }
}
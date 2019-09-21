package ninjaphenix.expandedstorage.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;

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
        lid.addCuboid(0, 0, 0, 14, 5, 14, 0);
        lid.addCuboid(6, -1, 14, 2, 4, 1, 0);
        lid.setRotationPoint(1, 9, 1);
        base.addCuboid(0, 0, 0, 14, 10, 14, 0);
        base.setRotationPoint(1, 0, 1);
    }

    public void setLidPitch(float pitch)
    {
        pitch = 1.0f - pitch;
        lid.pitch = -((1.0F - pitch * pitch * pitch) * 1.5707964F);
    }

    public void appendToBuffer(BufferBuilder bufferBuilder, float scale, int textureOffsetX, int textureOffsetY, Sprite texture)
    {
        base.method_22698(bufferBuilder, scale, textureOffsetX, textureOffsetY, texture);
        lid.method_22698(bufferBuilder, scale, textureOffsetX, textureOffsetY, texture);
    }
}
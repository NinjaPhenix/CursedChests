package ninjaphenix.expandedstorage.item;

import net.minecraft.text.TranslatableText;

public enum MutatorModes
{
    ROTATE(new TranslatableText("tooltip.expandedstorage.chest_mutator.rotate")),
    MERGE(new TranslatableText("tooltip.expandedstorage.chest_mutator.merge")),
    UNMERGE(new TranslatableText("tooltip.expandedstorage.chest_mutator.unmerge"));

    public final TranslatableText translation;

    MutatorModes(TranslatableText translation)
    {
        this.translation = translation;
    }
}

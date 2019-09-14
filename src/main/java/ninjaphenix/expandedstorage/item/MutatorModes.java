package ninjaphenix.expandedstorage.item;

import net.minecraft.text.TranslatableText;

public enum MutatorModes
{
    ROTATE(new TranslatableText("tooltip.expandedstorage.chest_mutator.rotate"), 1),
    MERGE(new TranslatableText("tooltip.expandedstorage.chest_mutator.merge"), 2),
    UNMERGE(new TranslatableText("tooltip.expandedstorage.chest_mutator.unmerge"), 0);

    public final TranslatableText translation;
    public final byte next;

    MutatorModes(TranslatableText translation, int next)
    {
        this.translation = translation;
        this.next = (byte) next;
    }
}

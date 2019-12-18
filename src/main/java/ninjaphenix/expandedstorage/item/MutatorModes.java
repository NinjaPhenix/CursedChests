package ninjaphenix.expandedstorage.item;

import net.minecraft.util.text.TranslationTextComponent;

public enum MutatorModes
{
	MERGE(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.merge"), 1),
	UNMERGE(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.unmerge"), 2),
	ROTATE(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.rotate"), 0);

	public final TranslationTextComponent translation;
	public final byte next;

	MutatorModes(TranslationTextComponent translation, int next)
	{
		this.translation = translation;
		this.next = (byte) next;
	}
}
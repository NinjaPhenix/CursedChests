package ninjaphenix.cursedchests.api.block;

import net.minecraft.block.enums.ChestType;
import net.minecraft.util.StringIdentifiable;

public enum CursedChestType implements StringIdentifiable
{
	SINGLE("single"), TOP("top"), BACK("back"), RIGHT("right"), BOTTOM("bottom"), FRONT("front"), LEFT("left");

	private final String name;

	CursedChestType(String string){ name = string; }

	public CursedChestType getOpposite()
	{
		switch (this)
		{
			case FRONT:
				return BACK;
			case BACK:
				return FRONT;
			case BOTTOM:
				return TOP;
			case TOP:
				return BOTTOM;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				return null;
		}
	}

	public static CursedChestType valueOf(ChestType type)
	{
		switch(type)
		{
			case SINGLE: return SINGLE;
			case RIGHT: return LEFT;
			case LEFT: return RIGHT;
			default: return null;
		}
	}

	public boolean isRenderedType(){ return this == FRONT || this == BOTTOM || this == LEFT || this == SINGLE; }

	public String asString(){ return name; }
}
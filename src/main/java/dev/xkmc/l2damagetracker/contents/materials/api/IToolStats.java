package dev.xkmc.l2damagetracker.contents.materials.api;

import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface IToolStats {

	int durability();

	int speed();

	int enchant();

	void configure(ITool tool, ItemAttributeModifiers.Builder builder);
}

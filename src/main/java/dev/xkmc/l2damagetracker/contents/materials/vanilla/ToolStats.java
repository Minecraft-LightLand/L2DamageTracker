package dev.xkmc.l2damagetracker.contents.materials.vanilla;

import dev.xkmc.l2damagetracker.contents.materials.api.ITool;
import dev.xkmc.l2damagetracker.contents.materials.api.IToolStats;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ToolStats(int durability, int speed, int base_damage, float base_speed, int enchant)
		implements IToolStats {

	@Override
	public void configure(ITool tool, ItemAttributeModifiers.Builder builder) {
		tool.configure(builder, tool.getDamage(base_damage), tool.getAtkSpeed(base_speed));
	}

}

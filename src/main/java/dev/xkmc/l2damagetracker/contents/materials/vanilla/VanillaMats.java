package dev.xkmc.l2damagetracker.contents.materials.vanilla;

import dev.xkmc.l2damagetracker.contents.materials.api.IMatToolType;
import dev.xkmc.l2damagetracker.contents.materials.api.ITool;
import dev.xkmc.l2damagetracker.contents.materials.api.IToolStats;
import dev.xkmc.l2damagetracker.contents.materials.api.ToolConfig;
import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record VanillaMats(Tiers tier) implements IMatToolType, IToolStats {

	@Override
	public int durability() {
		return tier.getUses();
	}

	@Override
	public int speed() {
		return Math.round(tier.getSpeed());
	}

	@Override
	public int enchant() {
		return tier.getEnchantmentValue();
	}

	@Override
	public void configure(ITool tool, ItemAttributeModifiers.Builder builder) {
		int dmg = tool.getDamage(Math.round(tier.getAttackDamageBonus()) + 4);
		float atkSpeed = tool.getAtkSpeed(1);
		tool.configure(builder, dmg, atkSpeed);
	}

	@Override
	public Tier getTier() {
		return tier;
	}

	@Override
	public IToolStats getToolStats() {
		return this;
	}

	@Override
	public ToolConfig getToolConfig() {
		return GenItemVanillaType.TOOL_GEN;
	}

	@Override
	public ExtraToolConfig getExtraToolConfig() {
		return new ExtraToolConfig();
	}

}

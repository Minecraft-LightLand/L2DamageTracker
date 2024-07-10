package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public interface ITool {

	int getDamage(int base_damage);

	float getAtkSpeed(float base_speed);

	Item create(Tier tier, Item.Properties prop, ExtraToolConfig config);

}

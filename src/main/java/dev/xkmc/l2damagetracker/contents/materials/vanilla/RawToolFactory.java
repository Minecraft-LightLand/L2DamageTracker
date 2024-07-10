package dev.xkmc.l2damagetracker.contents.materials.vanilla;

import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

@FunctionalInterface
public interface RawToolFactory {

	Item get(Tier tier, Item.Properties props, ExtraToolConfig config);
}

package dev.xkmc.l2damagetracker.contents.materials.api;

import net.minecraft.world.item.ArmorItem;

import java.util.Map;

public record ArmorStats(int durability, int[] protection, float tough, float kb, int enchant) {

	public Map<ArmorItem.Type, Integer> defense() {
		return Map.of(
				ArmorItem.Type.BOOTS, protection[0],
				ArmorItem.Type.LEGGINGS, protection[1],
				ArmorItem.Type.CHESTPLATE, protection[2],
				ArmorItem.Type.HELMET, protection[3]
		);
	}

}

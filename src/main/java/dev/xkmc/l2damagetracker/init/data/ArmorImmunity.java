package dev.xkmc.l2damagetracker.init.data;

import net.minecraft.world.effect.MobEffect;

import java.util.LinkedHashSet;
import java.util.List;

public record ArmorImmunity(LinkedHashSet<MobEffect> set, boolean full) {

	public static ArmorImmunity of(boolean full, MobEffect... effects) {
		return new ArmorImmunity(new LinkedHashSet<>(List.of(effects)), full);
	}

}

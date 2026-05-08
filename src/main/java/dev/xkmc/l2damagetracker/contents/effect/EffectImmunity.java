package dev.xkmc.l2damagetracker.contents.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.util.LinkedHashSet;
import java.util.List;

public record EffectImmunity(LinkedHashSet<Holder<MobEffect>> effects, Identifier id, int count) {

	public static EffectImmunity of(Identifier id, Holder<MobEffect>... effects) {
		return new EffectImmunity(new LinkedHashSet<>(List.of(effects)), id, 1);
	}

}

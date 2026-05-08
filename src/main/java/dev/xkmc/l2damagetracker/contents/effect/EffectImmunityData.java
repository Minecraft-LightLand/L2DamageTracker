package dev.xkmc.l2damagetracker.contents.effect;

import dev.xkmc.l2damagetracker.contents.equip.EquipTest;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record EffectImmunityData(LinkedHashSet<Holder<MobEffect>> set, Object2IntMap<Identifier> counts) {

	public static EffectImmunityData of(LivingEntity le) {
		EquipTest test = EquipTest.of(le);
		return test.compute(EffectImmunityData.class, () -> build(le.level().registryAccess(), test.getItemSet(le)));
	}

	private static EffectImmunityData build(RegistryAccess access, Set<Item> allItems) {
		Object2IntMap<Identifier> counts = new Object2IntOpenHashMap<>();
		List<EffectImmunity> list = new ArrayList<>();
		for (var e : allItems) {
			var imm = L2DamageTracker.EFFECT_IMMUNITY.get(access, e.builtInRegistryHolder());
			if (imm == null) continue;
			counts.mergeInt(imm.id(), 1, Integer::sum);
			list.add(imm);
		}
		LinkedHashSet<Holder<MobEffect>> eff = new LinkedHashSet<>();
		for (var e : list) {
			if (counts.getOrDefault(e.id(), 0) >= e.count()) {
				eff.addAll(e.effects());
			}
		}
		return new EffectImmunityData(eff, counts);
	}


}

package dev.xkmc.l2damagetracker.init.data;

import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.init.data.AttrDispEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.data.DataMapProvider;

public class DTAttributeConfigGen {

	public static void onDataMapGen(RegistrateDataMapProvider pvd) {
		var b = pvd.builder(L2Tabs.ATTRIBUTE_ENTRY.reg());
		add(b, L2DamageTracker.CRIT_RATE.key(), true, 11000);
		add(b, L2DamageTracker.CRIT_DMG.key(), true, 12000);
		add(b, L2DamageTracker.BOW_STRENGTH.key(), true, 13000);
		add(b, L2DamageTracker.EXPLOSION_FACTOR.key(), true, 16000);
		add(b, L2DamageTracker.FIRE_FACTOR.key(), true, 17000);
		add(b, L2DamageTracker.MAGIC_FACTOR.key(), true, 18000);
		add(b, L2DamageTracker.REDUCTION.key(), true, 23000);
		add(b, L2DamageTracker.ABSORB.key(), false, 24000);
		add(b, L2DamageTracker.REGEN.key(), true, 25000);
	}

	public static void add(DataMapProvider.Builder<AttrDispEntry, Attribute> b, ResourceKey<Attribute> attr, boolean perc, int order) {
		b.add(attr, new AttrDispEntry(perc, order, 0), false);
	}

}
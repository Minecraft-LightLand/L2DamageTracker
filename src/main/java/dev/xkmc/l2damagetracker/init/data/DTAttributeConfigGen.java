package dev.xkmc.l2damagetracker.init.data;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.init.data.AttrDispEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class DTAttributeConfigGen extends DataMapProvider {

	public DTAttributeConfigGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, lookup);
	}

	@Override
	protected void gather() {
		var b = builder(L2Tabs.ATTRIBUTE_ENTRY.reg());
		add(b, L2DamageTracker.CRIT_RATE.holder(), true, 11000);
		add(b, L2DamageTracker.CRIT_DMG.holder(), true, 12000);
		add(b, L2DamageTracker.BOW_STRENGTH.holder(), true, 13000);
		add(b, L2DamageTracker.EXPLOSION_FACTOR.holder(), true, 16000);
		add(b, L2DamageTracker.FIRE_FACTOR.holder(), true, 17000);
		add(b, L2DamageTracker.MAGIC_FACTOR.holder(), true, 18000);
		add(b, L2DamageTracker.REDUCTION.holder(), true, 23000);
		add(b, L2DamageTracker.ABSORB.holder(), false, 24000);
	}

	public static void add(Builder<AttrDispEntry, Attribute> b, Holder<Attribute> attr, boolean perc, int order) {
		var rl = attr.unwrapKey();
		assert rl.isPresent();
		b.add(rl.get(), new AttrDispEntry(perc, order, 0), false);
	}

}
package dev.xkmc.l2damagetracker.contents.damage;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Locale;
import java.util.function.Consumer;

public enum DefaultDamageState implements DamageState {
	BYPASS_ARMOR(DamageTypeTags.BYPASSES_ARMOR),
	BYPASS_MAGIC(L2DamageTypes.BYPASS_MAGIC.tags());

	private final TagKey<DamageType>[] tags;

	@SafeVarargs
	DefaultDamageState(TagKey<DamageType>... tags) {
		this.tags = tags;
	}

	@Override
	public void gatherTags(Consumer<TagKey<DamageType>> collector) {
		for (var tag : tags) {
			collector.accept(tag);
		}
	}

	@Override
	public void removeTags(Consumer<TagKey<DamageType>> collector) {

	}

	@Override
	public ResourceLocation getId() {
		return L2DamageTracker.loc(name().toLowerCase(Locale.ROOT));
	}

}

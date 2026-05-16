package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface DamageSourceExtra {

	void l2$enable(TagKey<DamageType> tag);

	void l2$disable(TagKey<DamageType> tag);

}

package dev.xkmc.l2damagetracker.compat;

import dev.xkmc.l2damagetracker.contents.damage.DamageState;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeWrapper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record SingletonDamageTypeWrapper(ResourceKey<DamageType> type) implements DamageTypeWrapper {

	@Override
	public boolean validState(DamageState state) {
		return false;
	}

	@Override
	public boolean isEnabled(DamageState state) {
		return false;
	}

	@Nullable
	@Override
	public DamageTypeWrapper enable(DamageState state) {
		return null;
	}

	@Override
	public DamageTypeWrapper toRoot() {
		return this;
	}

	@Override
	public DamageType getObject() {
		return null;
	}

	@Override
	public Set<DamageState> states() {
		return Set.of();
	}

}

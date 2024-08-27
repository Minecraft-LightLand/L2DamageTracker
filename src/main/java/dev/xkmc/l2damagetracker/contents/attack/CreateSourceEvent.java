package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.contents.damage.DamageState;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeWrapper;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

public class CreateSourceEvent extends Event {

	private final Registry<DamageType> registry;
	private final ResourceKey<DamageType> original;
	private final LivingEntity attacker;
	private final @Nullable Vec3 pos;
	private Entity direct;

	@Nullable
	private PlayerAttackCache playerAttackCache = null;

	@Nullable
	private DamageTypeWrapper result;

	public CreateSourceEvent(Registry<DamageType> registry, ResourceKey<DamageType> mobAttack, LivingEntity entity, @Nullable Entity direct, @Nullable Vec3 pos) {
		this.registry = registry;
		this.original = mobAttack;
		this.attacker = entity;
		this.pos = pos;
		this.direct = direct;
		this.result = DamageTypeRoot.ROOTS.get(original);
	}

	public ResourceKey<DamageType> getOriginal() {
		return original;
	}

	public LivingEntity getAttacker() {
		return attacker;
	}

	@Nullable
	public Vec3 getPos() {
		return pos;
	}

	@Nullable
	public Entity getDirect() {
		return direct;
	}

	public void setDirect(@Nullable Entity entity) {
		direct = entity;
	}

	@Nullable
	public DamageTypeWrapper getResult() {
		return result;
	}

	/**
	 * use enable instead
	 */
	@Deprecated
	public void setResult(DamageTypeWrapper result) {
		this.result = result;
	}

	@Nullable
	public PlayerAttackCache getPlayerAttackCache() {
		return playerAttackCache;
	}

	public void setPlayerAttackCache(PlayerAttackCache playerAttackCache) {
		this.playerAttackCache = playerAttackCache;
	}

	public Registry<DamageType> getRegistry() {
		return registry;
	}

	public void enable(DamageState state) {
		if (result == null) {
			L2DamageTracker.LOGGER.warn("DamageType " + original.location() + " is not mutable");
			return;
		}
		if (!result.validState(state)) {
			L2DamageTracker.LOGGER.warn("DamageType " + result.type().location() + " does not contain state " + state.getId());
			return;
		}
		if (result.isEnabled(state)) return;
		DamageTypeWrapper next = result.enable(state);
		if (next == null || !registry.containsKey(next.type())) {
			boolean all = true;
			boolean covered = false;
			for (DamageState old : result.states()) {
				all &= state.overrides(old);
				covered |= old.overrides(state);
			}
			if (all) {
				next = result.toRoot().enable(state);
			} else if (covered) {
				next = result;
			} else {
				L2DamageTracker.LOGGER.warn("DamageType " + result.type().location() + " cannot enable state " + state.getId());
				return;
			}
		}
		if (next != null) {
			result = next;
		}
	}


}

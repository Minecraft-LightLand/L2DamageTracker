package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;

public class OnDamageSourceModifyEvent extends LivingEvent {

	private final DamageSource source;
	@Nullable
	private PlayerAttackCache playerCache;

	public OnDamageSourceModifyEvent(LivingEntity le, DamageSource source) {
		super(le);
		this.source = source;
	}

	public DamageSource getSource() {
		return source;
	}

	public void setPlayerAttackCache(PlayerAttackCache cache) {
		playerCache = cache;
	}

	@Nullable
	public PlayerAttackCache getPlayerAttackCache() {
		return playerCache;
	}

}

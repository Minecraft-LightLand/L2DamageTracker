package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.function.BiConsumer;

public interface AttackListener {

	default void onPlayerAttack(PlayerAttackCache cache) {
	}

	default boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		return false;
	}

	default void setupProfile(DamageData attackCache, BiConsumer<LivingEntity, ItemStack> setupProfile) {
	}

	default boolean onAttack(DamageData.Attack cache) {
		return false;
	}

	default void onHurt(DamageData.Offence data) {
	}

	default void onHurtMaximized(DamageData.OffenceMax data) {
	}

	default void onDamage(DamageData.Defence data) {
	}

	default void onDamageFinalized(DamageData.DefenceMax data) {
	}

	default void onCreateSource(CreateSourceEvent event) {
	}

}

package dev.xkmc.l2damagetracker.events;

import dev.xkmc.l2damagetracker.contents.attack.*;
import dev.xkmc.l2damagetracker.contents.materials.generic.GenericTieredItem;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.function.BiConsumer;

public class GeneralAttackListener implements AttackListener {

	@Override
	public boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		Player player = event.getEntity();
		double cr = player.getAttributeValue(L2DamageTracker.CRIT_RATE.holder());
		double cd = player.getAttributeValue(L2DamageTracker.CRIT_DMG.holder());
		if (event.isVanillaCritical()) {
			event.setDamageMultiplier(event.getDamageMultiplier() / 1.5f * (float) (1 + cd));
			return true;
		} else if (player.getRandom().nextDouble() < cr) {
			event.setDamageMultiplier(event.getDamageMultiplier() * (float) (1 + cd));
			return true;
		}
		return false;
	}

	@Override
	public void setupProfile(DamageData cache, BiConsumer<LivingEntity, ItemStack> setup) {
		var le = cache.getAttacker();
		if (le != null) {
			if (cache.getSource().is(L2DamageTypes.DIRECT)) {
				setup.accept(le, le.getMainHandItem());
			} else {
				setup.accept(le, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void onHurt(DamageData cache, ItemStack weapon) {
		var event = cache.getLivingHurtEvent();
		assert event != null;
		if (weapon.getItem() instanceof GenericTieredItem item) {
			item.getExtraConfig().onDamage(cache, weapon);
		}
		var attacker = cache.getAttacker();
		if (attacker != null) {
			if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
				cache.addHurtModifier(DamageModifier.multTotal((float) L2DamageTracker.EXPLOSION_FACTOR.get().getWrappedValue(attacker)));
			}
			if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
				cache.addHurtModifier(DamageModifier.multTotal((float) L2DamageTracker.FIRE_FACTOR.get().getWrappedValue(attacker)));
			}
			if (event.getSource().is(L2DamageTypes.MAGIC)) {
				cache.addHurtModifier(DamageModifier.multTotal((float) L2DamageTracker.MAGIC_FACTOR.get().getWrappedValue(attacker)));
			}
		}
	}


	@Override
	public void onDamage(DamageDataExtra cache, ItemStack weapon) {
		var event = cache.getLivingDamageEvent();
		assert event != null;
		if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			var ins = cache.getAttackTarget().getAttribute(L2DamageTracker.REDUCTION.get());
			if (ins != null) {
				float val = (float) ins.getValue();
				cache.addDealtModifier(DamageModifier.multAttr(val));
			}
			ins = cache.getAttackTarget().getAttribute(L2DamageTracker.ABSORB.get());
			if (ins != null) {
				float val = (float) ins.getValue();
				cache.addDealtModifier(DamageModifier.add(-val));
				cache.addDealtModifier(DamageModifier.nonlinearMiddle(943, e -> Math.max(0, e)));
			}
		}
	}

}

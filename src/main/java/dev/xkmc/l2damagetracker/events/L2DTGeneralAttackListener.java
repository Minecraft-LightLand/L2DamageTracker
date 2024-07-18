package dev.xkmc.l2damagetracker.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.PlayerAttackCache;
import dev.xkmc.l2damagetracker.contents.materials.generic.GenericTieredItem;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.function.BiConsumer;

public class L2DTGeneralAttackListener implements AttackListener {

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
	public void onHurt(DamageData.Offence data) {
		if (data.getWeapon().getItem() instanceof GenericTieredItem item) {
			item.getExtraConfig().onDamage(data, data.getWeapon());
		}
		var attacker = data.getAttacker();
		if (attacker != null) {
			if (data.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
				var ins = attacker.getAttribute(L2DamageTracker.EXPLOSION_FACTOR.holder());
				if (ins != null)
					data.addHurtModifier(DamageModifier.multTotal((float) ins.getValue(),
							L2DamageTracker.EXPLOSION_FACTOR.key().location()));
			}
			if (data.getSource().is(DamageTypeTags.IS_FIRE)) {
				var ins = attacker.getAttribute(L2DamageTracker.FIRE_FACTOR.holder());
				if (ins != null) data.addHurtModifier(DamageModifier.multTotal((float) ins.getValue(),
						L2DamageTracker.FIRE_FACTOR.key().location()));
			}
			if (data.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
				var ins = attacker.getAttribute(L2DamageTracker.MAGIC_FACTOR.holder());
				if (ins != null) data.addHurtModifier(DamageModifier.multTotal((float) ins.getValue(),
						L2DamageTracker.MAGIC_FACTOR.key().location()));
			}
		}
	}

	@Override
	public void onDamage(DamageData.Defence data) {
		if (!data.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			var ins = data.getTarget().getAttribute(L2DamageTracker.REDUCTION.holder());
			if (ins != null) {
				float val = (float) ins.getValue();
				data.addDealtModifier(DamageModifier.multAttr(val,
						L2DamageTracker.REDUCTION.key().location()));
			}
			ins = data.getTarget().getAttribute(L2DamageTracker.ABSORB.holder());
			if (ins != null) {
				float val = (float) ins.getValue();
				data.addDealtModifier(DamageModifier.add(-val,
						L2DamageTracker.ABSORB.key().location()));
				data.addDealtModifier(DamageModifier.nonlinearMiddle(943, e -> Math.max(0, e),
						L2DamageTracker.ABSORB.key().location().withSuffix("_prevent_underflow")));
			}
		}
	}

}

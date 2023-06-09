package dev.xkmc.l2damagetracker.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.PlayerAttackCache;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraToolConfig;
import dev.xkmc.l2damagetracker.contents.materials.generic.GenericTieredItem;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.BiConsumer;

public class GeneralAttackListener implements AttackListener {

	@Override
	public boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		Player player = event.getEntity();
		double cr = player.getAttributeValue(L2DamageTracker.CRIT_RATE.get());
		double cd = player.getAttributeValue(L2DamageTracker.CRIT_DMG.get());
		if (event.isVanillaCritical()) {
			event.setDamageModifier((float) (event.getDamageModifier() + cd - 0.5));
		} else if (player.getRandom().nextDouble() < cr) {
			event.setDamageModifier((float) (event.getDamageModifier() + cd - 0.5));
			event.setResult(Event.Result.ALLOW);
			return true;
		}
		return false;
	}

	@Override
	public void onCreateSource(CreateSourceEvent event) {
		if (event.getAttacker().getMainHandItem().getItem() instanceof GenericTieredItem gen) {
			if (event.getRegistry().getHolderOrThrow(event.getOriginal()).is(L2DamageTypes.MATERIAL_MUX)) {
				ExtraToolConfig config = gen.getExtraConfig();
				if (config.bypassMagic) event.enable(DefaultDamageState.BYPASS_MAGIC);
				if (config.bypassArmor) event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

	@Override
	public void setupProfile(AttackCache cache, BiConsumer<LivingEntity, ItemStack> setup) {
		if (cache.getLivingAttackEvent() != null) {
			DamageSource source = cache.getLivingAttackEvent().getSource();
			if (source.getEntity() instanceof LivingEntity le) {
				if (source.getDirectEntity() == le) {
					setup.accept(le, le.getMainHandItem());
				} else {
					setup.accept(le, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public void onHurt(AttackCache cache, ItemStack weapon) {
		assert cache.getLivingHurtEvent() != null;
		if (weapon.getItem() instanceof GenericTieredItem item) {
			item.getExtraConfig().onDamage(cache, weapon);
		}
	}

}

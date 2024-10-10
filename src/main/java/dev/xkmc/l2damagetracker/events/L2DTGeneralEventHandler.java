package dev.xkmc.l2damagetracker.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.xkmc.l2core.base.effects.ForceAddEffectEvent;
import dev.xkmc.l2damagetracker.contents.logging.LogHelper;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.ArmorImmunity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = L2DamageTracker.MODID, bus = EventBusSubscriber.Bus.GAME)
public class L2DTGeneralEventHandler {

	@SubscribeEvent
	public static void onCommandRegister(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("damagetracker");
		LogHelper.buildCommand(base);
		event.getDispatcher().register(base);
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		LogHelper.tick(event.getServer());
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onHeal(LivingHealEvent event) {
		var ins = event.getEntity().getAttribute(L2DamageTracker.REGEN);
		if (ins == null) return;
		var factor = ins.getValue();
		event.setAmount((float) (event.getAmount() * factor));
	}

	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Post event) {
		if (!(event.getEntity() instanceof LivingEntity le)) return;
		if (le.getActiveEffectsMap().isEmpty()) return;
		if (!(le instanceof Player) || le.tickCount % 10 != 0) return;//Do force remove for player only
		var access = event.getEntity().level().registryAccess();
		Map<ArmorImmunity, Integer> map = new HashMap<>();
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
			ItemStack stack = le.getItemBySlot(slot);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ArmorItem armor)) continue;
			ArmorImmunity e = L2DamageTracker.ARMOR.get(access, armor.getMaterial());
			if (e == null) continue;
			map.compute(e, (k, v) -> (v == null ? 0 : v) + 1);
		}
		Set<Holder<MobEffect>> set = new HashSet<>();
		for (var ent : map.entrySet()) {
			var e = ent.getKey();
			if (e.full() && ent.getValue() < 4) continue;
			for (var eff : le.getActiveEffectsMap().keySet()) {
				if (e.set().contains(eff.value())) {
					set.add(eff);
				}
			}
		}
		for (var e : set) {
			le.removeEffect(e);
		}
	}

	private static boolean immune(RegistryAccess access, LivingEntity le, MobEffect effect) {
		ArmorImmunity set = null;
		int count = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
			ItemStack stack = le.getItemBySlot(slot);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ArmorItem armor)) continue;
			ArmorImmunity e = L2DamageTracker.ARMOR.get(access, armor.getMaterial());
			if (e == null) continue;
			if (!e.set().contains(effect)) continue;
			if (set == null) set = e;
			if (set == e) count++;
			if (!e.full()) {
				return true;
			}
		}
		return set != null && count >= 4;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onForceEffec(ForceAddEffectEvent event) {
		var ins = event.getEffectInstance();
		var access = event.getEntity().level().registryAccess();
		if (immune(access, event.getEntity(), ins.getEffect().value())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPotionTest(MobEffectEvent.Applicable event) {
		var ins = event.getEffectInstance();
		if (ins == null) return;
		var access = event.getEntity().level().registryAccess();
		if (immune(access, event.getEntity(), ins.getEffect().value())) {
			event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
		}
	}

}

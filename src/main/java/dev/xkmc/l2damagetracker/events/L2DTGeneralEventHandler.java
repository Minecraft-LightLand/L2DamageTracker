package dev.xkmc.l2damagetracker.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.xkmc.l2core.base.effects.ForceAddEffectEvent;
import dev.xkmc.l2damagetracker.contents.effect.EffectImmunityData;
import dev.xkmc.l2damagetracker.contents.logging.LogHelper;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Set;

@EventBusSubscriber(modid = L2DamageTracker.MODID)
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
		Set<Holder<MobEffect>> set = EffectImmunityData.of(le).set();
		for (var e : set) {
			le.removeEffect(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onForceEffect(ForceAddEffectEvent event) {
		var ins = event.getEffectInstance();
		if (EffectImmunityData.of(event.getEntity()).set().contains(ins.getEffect())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPotionTest(MobEffectEvent.Applicable event) {
		var ins = event.getEffectInstance();
		if (ins == null) return;
		if (EffectImmunityData.of(event.getEntity()).set().contains(ins.getEffect())) {
			event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
		}
	}

}

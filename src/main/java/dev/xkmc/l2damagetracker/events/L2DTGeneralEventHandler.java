package dev.xkmc.l2damagetracker.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.xkmc.l2damagetracker.contents.attack.LogHelper;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.ArmorImmunity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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

	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Post event) {
		var e = event.getEntity();
		if (!(e instanceof LivingEntity le)) return;
		if (le.getActiveEffectsMap().isEmpty()) return;
		//TODO eff remove
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPotionTest(MobEffectEvent.Applicable event) {
		var ins = event.getEffectInstance();
		if (ins == null) return;
		var access = event.getEntity().level().registryAccess();
		ArmorImmunity set = null;
		int count = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ArmorItem armor)) continue;
			ArmorImmunity e = L2DamageTracker.ARMOR.get(access, armor.getMaterial());
			if (e == null) continue;
			if (!e.set().contains(ins.getEffect().value())) continue;
			if (set == null) set = e;
			if (set == e) count++;
			if (!e.full()) {
				event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
				return;
			}
		}
		if (set != null && count >= 4) {
			event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
		}
	}

}

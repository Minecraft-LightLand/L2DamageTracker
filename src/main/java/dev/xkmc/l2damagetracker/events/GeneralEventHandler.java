package dev.xkmc.l2damagetracker.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.xkmc.l2damagetracker.contents.attack.LogHelper;
import dev.xkmc.l2damagetracker.contents.materials.generic.GenericArmorItem;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = L2DamageTracker.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GeneralEventHandler {

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
	public static void onPotionTest(MobEffectEvent.Applicable event) {
		var ins = event.getEffectInstance();
		if (ins == null) return;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof GenericArmorItem armor) {
					if (armor.getConfig().immuneToEffect(stack, armor, ins)) {
						event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
						return;
					}
				}
			}
		}
	}

}

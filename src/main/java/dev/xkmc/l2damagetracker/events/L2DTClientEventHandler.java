package dev.xkmc.l2damagetracker.events;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.ArmorImmunity;
import dev.xkmc.l2damagetracker.init.data.L2DTLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.TreeMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2DamageTracker.MODID, bus = EventBusSubscriber.Bus.GAME)
public class L2DTClientEventHandler {

	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		var player = event.getEntity();
		if (player == null) return;
		if (!(event.getItemStack().getItem() instanceof ArmorItem item)) return;
		var mat = item.getMaterial();
		var access = player.level().registryAccess();
		ArmorImmunity config = L2DamageTracker.ARMOR.get(access, mat);
		if (config == null) return;
		TreeMap<ResourceLocation, MobEffect> map = new TreeMap<>();
		for (var e : config.set()) {
			map.put(BuiltInRegistries.MOB_EFFECT.getKey(e), e);
		}
		MutableComponent comp = config.full() ? L2DTLangData.ARMOR_IMMUNE_SET.get() : L2DTLangData.ARMOR_IMMUNE.get();
		boolean comma = false;
		for (var e : map.values()) {
			if (comma) comp = comp.append(", ");
			comma = true;
			comp = comp.append(Component.translatable(e.getDescriptionId()).withStyle(e.getCategory().getTooltipFormatting()));
		}
		boolean enabled = true;
		if (config.full()) {
			int count = 0;
			for (var e : EquipmentSlot.values()) {
				if (e.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
				if (player.getItemBySlot(e).getItem() instanceof ArmorItem ar) {
					if (ar.getMaterial().value() == mat.value()) count++;//TODO fix
				}
			}
			enabled = count >= 4;
		}
		event.getToolTip().add(comp.withStyle(enabled ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
	}

}

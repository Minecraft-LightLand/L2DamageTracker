package dev.xkmc.l2damagetracker.events;

import dev.xkmc.l2damagetracker.contents.effect.EffectImmunity;
import dev.xkmc.l2damagetracker.contents.effect.EffectImmunityData;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DTLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.TreeMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2DamageTracker.MODID)
public class L2DTClientEventHandler {

	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		var player = event.getEntity();
		if (player == null) return;

		var access = player.level().registryAccess();
		EffectImmunity config = L2DamageTracker.EFFECT_IMMUNITY.get(access, event.getItemStack().typeHolder());
		if (config == null) return;
		TreeMap<Identifier, Holder<MobEffect>> map = new TreeMap<>();
		for (var e : config.effects()) {
			map.put(e.unwrapKey().orElseThrow().identifier(), e);
		}
		MutableComponent comp = config.count() > 1 ? L2DTLangData.ARMOR_IMMUNE_SET.get(config.count()) : L2DTLangData.ARMOR_IMMUNE.get();
		boolean comma = false;
		for (var e : map.values()) {
			if (comma) comp = comp.append(", ");
			comma = true;
			comp = comp.append(Component.translatable(e.value().getDescriptionId()).withStyle(e.value().getCategory().getTooltipFormatting()));
		}
		boolean enabled = true;
		if (config.count() > 1) {
			int count = EffectImmunityData.of(player).counts().getOrDefault(config.id(), 0);
			enabled = count >= config.count();
		}
		event.getToolTip().add(comp.withStyle(enabled ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
	}

}

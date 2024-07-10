package dev.xkmc.l2damagetracker.contents.curios;

import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

public interface L2Totem {

	default void trigger(LivingEntity self, ItemStack holded, TotemHelper.TotemSlot second, DamageSource source) {
		trigger(self, holded, second);
	}

	default void trigger(LivingEntity self, ItemStack holded, Consumer<ItemStack> second) {
		L2DamageTracker.PACKET_HANDLER.toTrackingPlayers(TotemUseToClient.of(self, holded), self);
		holded.shrink(1);
		self.setHealth(1.0F);
		self.removeAllEffects();
		self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
		self.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
		self.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
	}

	default boolean allow(LivingEntity self, ItemStack stack, DamageSource source) {
		return allow(self, source);
	}

	default boolean allow(LivingEntity self, DamageSource source) {
		return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
	}

	@OnlyIn(Dist.CLIENT)
	default void onClientTrigger(Entity entity, ItemStack item) {
		Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
		entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
		if (entity == Proxy.getClientPlayer()) {
			Minecraft.getInstance().gameRenderer.displayItemActivation(item);
		}
	}

	default boolean isValid(LivingEntity self, ItemStack stack, TotemHelper.TotemSlot slot) {
		return true;
	}

}

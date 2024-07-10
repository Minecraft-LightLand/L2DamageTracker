package dev.xkmc.l2damagetracker.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {//TODO remove in future

	@Inject(at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;getDamageMultiplier()F"), method = "attack")
	public void l2damagetracker$attack$allowSweep(Entity target, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef flag2) {
		flag2.set(false);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"), method = "attack")
	public void l2damagetracker$attack$critParticle(Entity target, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef flag2, @Local CriticalHitEvent hitResult) {
		flag2.set(hitResult != null);
	}

}

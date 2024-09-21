package dev.xkmc.l2damagetracker.mixin;

import dev.xkmc.l2damagetracker.contents.attack.DamageContainerExtra;
import dev.xkmc.l2damagetracker.contents.attack.DamageDataExtra;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageContainer.class)
public class DamageContainerMixin implements DamageContainerExtra {

	@Unique
	private final DamageDataExtra l2damagetracker$extra = new DamageDataExtra();

	@Override
	public @NotNull DamageDataExtra l2damagetracker$getExtra() {
		return l2damagetracker$extra;
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	public void l2damagetracker$onInit(DamageSource source, float originalDamage, CallbackInfo ci) {
		l2damagetracker$extra.init(source, originalDamage);
	}

	@Inject(at = @At("HEAD"), method = "setNewDamage")
	public void l2damagetracker$setNewDamage(float damage, CallbackInfo ci) {
		l2damagetracker$extra.onSetNewDamage(damage);
	}

}

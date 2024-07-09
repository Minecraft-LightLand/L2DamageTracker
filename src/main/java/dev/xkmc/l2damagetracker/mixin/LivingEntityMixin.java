package dev.xkmc.l2damagetracker.mixin;

import dev.xkmc.l2damagetracker.contents.curios.TotemHelper;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
	public void l2damagetracker$checkTotemDeathProtection$addCustomTotem(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = Wrappers.cast(this);
		if (TotemHelper.process(self, pDamageSource)) cir.setReturnValue(true);
	}

}

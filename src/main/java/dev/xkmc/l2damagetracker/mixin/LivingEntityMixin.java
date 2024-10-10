package dev.xkmc.l2damagetracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2damagetracker.contents.attack.DamageContainerExtra;
import dev.xkmc.l2damagetracker.contents.curios.TotemHelper;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Stack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow
	@Nullable
	protected Stack<DamageContainer> damageContainers;

	@Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
	public void l2damagetracker$checkTotemDeathProtection$addCustomTotem(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = Wrappers.cast(this);
		if (TotemHelper.process(self, pDamageSource)) cir.setReturnValue(true);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Ljava/util/Stack;pop()Ljava/lang/Object;"), method = "hurt")
	public <E> E l2damagetracker$hurt$end(Stack<E> instance, Operation<E> original) {
		E out = original.call(instance);
		if (out instanceof DamageContainerExtra cont) {
			cont.l2damagetracker$getExtra().end();
		}
		return out;
	}

}

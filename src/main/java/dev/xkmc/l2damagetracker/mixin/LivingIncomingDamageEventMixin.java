package dev.xkmc.l2damagetracker.mixin;

import dev.xkmc.l2damagetracker.contents.immunity.ImmunityData;
import dev.xkmc.l2damagetracker.contents.immunity.ImmunityDataExtra;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingIncomingDamageEvent.class)
public abstract class LivingIncomingDamageEventMixin extends LivingEvent implements ICancellableEvent, ImmunityDataExtra {

	public LivingIncomingDamageEventMixin(LivingEntity entity) {
		super(entity);
	}

	@Unique
	private final ImmunityData l2damagetracker$extra = new ImmunityData();

	@Override
	public @NotNull ImmunityData l2damagetracker$getExtra() {
		return l2damagetracker$extra;
	}

	@Override
	public void setCanceled(boolean canceled) {
		ICancellableEvent.super.setCanceled(canceled);
	}

	@Inject(at = @At("HEAD"), method = "setCanceled")
	public void l2damagetracker$setInvulnerable$log(boolean val, CallbackInfo ci) {
		if (val) l2damagetracker$extra.log();
	}

}

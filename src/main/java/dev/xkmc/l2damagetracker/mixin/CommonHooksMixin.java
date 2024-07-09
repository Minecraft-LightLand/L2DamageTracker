package dev.xkmc.l2damagetracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2damagetracker.contents.attack.DamageContainerExtra;
import dev.xkmc.l2damagetracker.contents.attack.DamageDataExtra;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommonHooks.class)
public class CommonHooksMixin {

	@WrapOperation(method = "onEntityIncomingDamage", at = @At(value = "INVOKE", target =
			"Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
	private static Event l2damagetracker$postEntityIncomingDamageEvent(IEventBus instance, Event event, Operation<? extends Event> original) {
		LivingIncomingDamageEvent evt = (LivingIncomingDamageEvent) event;
		DamageDataExtra cont = DamageContainerExtra.get((evt).getContainer());
		cont.onIncoming(evt, e -> original.call(instance, e));
		return evt;
	}

	@WrapOperation(method = "onLivingDamagePre", at = @At(value = "INVOKE", target =
			"Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
	private static Event l2damagetracker$postEntityDamagePreEvent(IEventBus instance, Event event, Operation<? extends Event> original) {
		DamageContainerExtra.get(((LivingDamageEvent.Pre) event).getContainer()).onDamagePre((LivingDamageEvent.Pre) event);
		Event ans = original.call(instance, event);
		DamageContainerExtra.get(((LivingDamageEvent.Pre) event).getContainer()).onDamagePost((LivingDamageEvent.Pre) event);
		return ans;
	}

}

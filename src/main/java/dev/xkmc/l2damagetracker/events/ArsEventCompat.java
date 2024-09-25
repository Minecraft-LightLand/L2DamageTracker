package dev.xkmc.l2damagetracker.events;

import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;

public class ArsEventCompat {

	@SubscribeEvent
	public static void onArsAttack(SpellDamageEvent.Pre event) {
		if (event.damageSource.getMsgId().equals("player_attack")) {
			var old = event.damageSource;
			event.damageSource = new DamageSource(
					event.caster.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.INDIRECT_MAGIC),
					old.getDirectEntity(),
					old.getEntity(),
					old.getSourcePosition());
		}
	}

}

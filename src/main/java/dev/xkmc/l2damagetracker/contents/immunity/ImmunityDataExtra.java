package dev.xkmc.l2damagetracker.contents.immunity;

import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public interface ImmunityDataExtra {

	static ImmunityData get(EntityInvulnerabilityCheckEvent cont) {
		return ((ImmunityDataExtra) cont).l2damagetracker$getExtra();
	}

	static ImmunityData get(LivingIncomingDamageEvent cont) {
		return ((ImmunityDataExtra) cont).l2damagetracker$getExtra();
	}

	static ImmunityData get(LivingDamageEvent.Pre cont) {
		return ((ImmunityDataExtra) cont).l2damagetracker$getExtra();
	}

	ImmunityData l2damagetracker$getExtra();

}

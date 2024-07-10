package dev.xkmc.l2damagetracker.events;

public class ArsEventCompat {

	/* TODO
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
	}*/

}

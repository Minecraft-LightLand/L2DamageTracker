package dev.xkmc.l2damagetracker.contents.immunity;

import dev.xkmc.l2damagetracker.contents.logging.AttackLogEntry;
import dev.xkmc.l2damagetracker.contents.logging.ImmunityLogEntry;
import dev.xkmc.l2damagetracker.contents.logging.LoggingBase;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;

public class ImmunityData {

	private LoggingBase entry;

	public void attach(AttackLogEntry log) {
		entry = log;
	}

	public void setup(EntityInvulnerabilityCheckEvent evt) {
		if (evt.getEntity() instanceof LivingEntity le) {
			entry = new ImmunityLogEntry(evt.getSource(), le);
		}
	}

	public void log() {
		if (entry != null)
			entry.logImmunity();
	}

	public void end() {
		if (entry instanceof ImmunityLogEntry imm)
			imm.end();
	}

}

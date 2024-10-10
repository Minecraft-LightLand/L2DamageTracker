package dev.xkmc.l2damagetracker.contents.logging;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public class ImmunityLogEntry extends LoggingBase {

	public ImmunityLogEntry(DamageSource source, LivingEntity target) {
		super(source, target, source.getEntity() instanceof LivingEntity le ? le : null);
	}

	public void end() {
		output.add("------ Damage Tracker Profile END ------");
		if (info) {
			for (var e : output) {
				L2DamageTracker.LOGGER.info(e);
			}
		}
		for (var target : saves) {
			String str = "immunity/" + target.path();
			if (target.client() != null) {
				L2DamageTracker.PACKET_HANDLER.toClientPlayer(new SendLogPacket(str, new ArrayList<>(output)), target.client());
			} else {
				writeToFile(str, output);
			}
		}
	}

}

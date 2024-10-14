package dev.xkmc.l2damagetracker.contents.logging;

import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttackLogEntry extends LoggingBase {

	public enum Stage {
		INCOMING, INCOMING_POST, DAMAGE, DAMAGE_POST
	}

	public static AttackLogEntry of(DamageSource source, LivingEntity target, @Nullable LivingEntity attacker) {
		return new AttackLogEntry(source, target, attacker);
	}

	private final Map<DamageModifier, String> modifiers = new HashMap<>();
	private Stage lastStage;

	private AttackLogEntry(DamageSource source, LivingEntity target, @Nullable LivingEntity attacker) {
		super(source, target, attacker);
	}

	public void log(Stage stage, float amount) {
		if (!log) return;
		output.add("Stage " + stage.name() + ": val = " + amount);
		lastStage = stage;
	}

	public void logImmunity(AttackListener l) {
		if (!log) return;
		output.add("<Immunity> Damage cancelled by " + l.getClass().getName());
	}

	public void logNoImmunity() {
		if (!log) return;
		output.add("<Immunity> Damage marked non-cancellable by " + getStackTrace());
	}

	public void end() {
		output.add("------ Damage Tracker Profile END ------");
		if (info) {
			for (var e : output) {
				L2DamageTracker.LOGGER.info(e);
			}
		}
		for (var target : saves) {
			String str = target.path();
			if (lastStage != Stage.DAMAGE_POST) {
				str += "-cancelled";
			}
			if (target.client() != null) {
				L2DamageTracker.PACKET_HANDLER.toClientPlayer(new SendLogPacket(str, new ArrayList<>(output)), target.client());
			} else {
				writeToFile(str, output);
			}
		}
	}

	@Nullable
	public AttackLogEntry initModifiers() {
		modifiers.clear();
		return trace ? this : null;
	}

	public void recordModifier(DamageModifier mod) {
		if (!trace) return;
		modifiers.put(mod, mod.id() + " --- " + getStackTrace());
	}

	public void startLayer(DamageModifier.Order key, float val) {
		output.add("| - Layer " + key.name() + " start, val = " + val);
	}

	public void processModifier(DamageModifier e, String info) {
		var s = modifiers.get(e);
		if (s == null) s = e.id().toString();
		output.add("| - | " + info + ", source = " + s);
	}

	public void endLayer(DamageModifier.Order key, float val) {
		output.add("| - Layer " + key.name() + " end, val = " + val);
	}

	public void eventLayer(Event event, float val) {
		if (!log) return;
		String info = event.getClass().getSimpleName() + ", source = " + getStackTrace();
		output.add("| - | -> " + val + ", source = " + info);
	}

}

package dev.xkmc.l2damagetracker.init.data;

import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.neoforged.neoforge.common.ModConfigSpec;

public class L2DamageTrackerConfig {

	public static class Server extends ConfigInit {

		public final ModConfigSpec.BooleanValue printDamageTrace;
		public final ModConfigSpec.BooleanValue savePlayerAttack;
		public final ModConfigSpec.BooleanValue savePlayerHurt;

		Server(Builder builder) {
			markL2();
			printDamageTrace = builder
					.text("Damage Trace Logging")
					.comment("Print damage trace tracked by damage tracker")
					.define("printDamageTrace", false);
			savePlayerAttack = builder
					.text("Debug player attacks")
					.comment("Save player attack damage trace to file under logs/damage_tracker/")
					.define("savePlayerAttack", false);
			savePlayerHurt = builder
					.text("Debug attacks on player")
					.comment("Save player hurt damage trace to file under logs/damage_tracker/")
					.define("savePlayerDamaged", false);
		}

	}

	public static final Server SERVER = L2DamageTracker.REGISTRATE.registerSynced(Server::new);

	public static void init() {
	}

}

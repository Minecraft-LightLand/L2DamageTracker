package dev.xkmc.l2damagetracker.init.data;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class L2DamageTrackerConfig {

	public static class Client {

		Client(ModConfigSpec.Builder builder) {
		}

	}

	public static class Server {

		public final ModConfigSpec.BooleanValue enableCyclicDamageEventInterrupt;
		public final ModConfigSpec.IntValue cyclicDamageThreshold;
		public final ModConfigSpec.BooleanValue muteCyclicDamageInterrupt;
		public final ModConfigSpec.BooleanValue printDamageTrace;
		public final ModConfigSpec.BooleanValue savePlayerAttack;
		public final ModConfigSpec.BooleanValue savePlayerHurt;

		Server(ModConfigSpec.Builder builder) {
			enableCyclicDamageEventInterrupt = builder
					.comment("Allows L2DamageTracker to detect and prevent cyclic damage events")
					.define("enableCyclicDamageEventInterrupt", false);
			cyclicDamageThreshold = builder
					.comment("Cyclic Damage Interruption threshold")
					.defineInRange("cyclicDamageThreshold", 1, 1, 1000);
			muteCyclicDamageInterrupt = builder.comment("Mute error log lines for cyclic damage")
					.define("muteCyclicDamageInterrupt", false);
			printDamageTrace = builder.comment("Print damage trace tracked by damage tracker")
					.define("printDamageTrace", false);
			savePlayerAttack = builder.comment("Save player attack damage trace")
					.define("savePlayerAttack", false);
			savePlayerHurt = builder.comment("Save player hurt damage trace")
					.define("savePlayerDamaged", false);


		}

	}

	public static final ModConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	public static final ModConfigSpec SERVER_SPEC;
	public static final Server SERVER;

	static {
		final Pair<Client, ModConfigSpec> client = new ModConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();

		final Pair<Server, ModConfigSpec> common = new ModConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = common.getRight();
		SERVER = common.getLeft();
	}

	/**
	 * Registers any relevant listeners for config
	 */
	public static void init() {
		register(ModConfig.Type.CLIENT, CLIENT_SPEC);
		register(ModConfig.Type.SERVER, SERVER_SPEC);
	}

	private static void register(ModConfig.Type type, IConfigSpec spec) {
		var mod = ModLoadingContext.get().getActiveContainer();
		String path = "l2_configs/" + mod.getModId() + "-" + type.extension() + ".toml";
		mod.registerConfig(type, spec, path);
	}


}

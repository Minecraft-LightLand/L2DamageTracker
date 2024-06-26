package dev.xkmc.l2damagetracker.init.data;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class L2DamageTrackerConfig {

	public static class Client {

		Client(ForgeConfigSpec.Builder builder) {
		}

	}

	public static class Common {

		public final ForgeConfigSpec.BooleanValue enableCyclicDamageEventInterrupt;
		public final ForgeConfigSpec.IntValue cyclicDamageThreshold;
		public final ForgeConfigSpec.BooleanValue muteCyclicDamageInterrupt;
		public final ForgeConfigSpec.BooleanValue printDamageTrace;
		public final ForgeConfigSpec.BooleanValue savePlayerAttack;
		public final ForgeConfigSpec.BooleanValue savePlayerHurt;

		Common(ForgeConfigSpec.Builder builder) {
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

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		final Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();

		final Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = common.getRight();
		COMMON = common.getLeft();
	}

	/**
	 * Registers any relevant listeners for config
	 */
	public static void init() {
		register(ModConfig.Type.CLIENT, CLIENT_SPEC);
		register(ModConfig.Type.COMMON, COMMON_SPEC);
	}

	private static void register(ModConfig.Type type, IConfigSpec<?> spec) {
		var mod = ModLoadingContext.get().getActiveContainer();
		String path = "l2_configs/" + mod.getModId() + "-" + type.extension() + ".toml";
		ModLoadingContext.get().registerConfig(type, spec, path);
	}


}

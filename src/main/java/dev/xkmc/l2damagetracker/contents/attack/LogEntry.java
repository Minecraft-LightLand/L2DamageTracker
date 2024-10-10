package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class LogEntry {

	public enum Stage {
		INCOMING, INCOMING_POST, DAMAGE, DAMAGE_POST
	}

	public record Target(String path, @Nullable ServerPlayer client) {
	}

	public static LogEntry of(DamageSource source, LivingEntity target, @Nullable LivingEntity attacker) {
		return new LogEntry(source, target, attacker);
	}

	private static String path(Player player, @Nullable LivingEntity other, String type, String time) {
		String otherType;
		if (other == null) {
			otherType = "null";
		} else {
			ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(other.getType());
			otherType = rl.getPath().replaceAll("/", "_");
		}
		return player.getScoreboardName() + "-" + type + "/" + otherType + "/" + time;
	}

	public static void writeToFile(String str, List<String> file) {
		var path = FMLPaths.GAMEDIR.get().resolve("logs/damage_tracker/" + str + ".txt");
		write(path, e -> file.forEach(e::println));
	}

	private final DamageSource source;
	private final LivingEntity target;
	@Nullable
	private final LivingEntity attacker;
	private final String time;
	private final boolean log, info, trace;
	private final List<String> output = new ArrayList<>();
	private final Map<DamageModifier, String> modifiers = new HashMap<>();
	private final List<Target> saves = new ArrayList<>();

	private Stage lastStage;

	private LogEntry(DamageSource source, LivingEntity target, @Nullable LivingEntity attacker) {
		this.source = source;
		this.target = target;
		this.attacker = attacker;
		this.time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());
		info = L2DamageTrackerConfig.SERVER.printDamageTrace.get();
		if (target instanceof ServerPlayer player)
			LogHelper.savePlayerHurt(player).save(saves, path(player, attacker, "hurt", time));
		if (attacker instanceof ServerPlayer player)
			LogHelper.savePlayerAttack(player).save(saves, path(player, target, "attack", time));
		trace = !saves.isEmpty();
		log = info || trace;
		if (log) {
			output.add("------ Damage Tracker Profile START ------");
			output.add("Attacked Entity: " + target);
			output.add("Attacker Entity: " + attacker);
			output.add("Damage Source: " + source.typeHolder().unwrapKey()
					.map(e -> e.location().toString())
					.orElseGet(() -> source.type().msgId()));
		}
	}

	public void log(Stage stage, float amount) {
		if (!log) return;
		output.add("Stage " + stage.name() + ": val = " + amount);
		lastStage = stage;
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
	public LogEntry initModifiers() {
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
		output.add("| - | " + info + ", source = " + modifiers.get(e));
	}

	public void endLayer(DamageModifier.Order key, float val) {
		output.add("| - Layer " + key.name() + " end, val = " + val);
	}

	public void eventLayer(Event event, float val) {
		if (!log) return;
		String info = event.getClass().getSimpleName() + ", source = " + getStackTrace();
		output.add("| - | -> " + val + ", source = " + info);
	}

	private static void write(Path path, Consumer<PrintStream> cons) {
		PrintStream stream = null;
		try {
			stream = getStream(path);
			cons.accept(stream);
		} catch (Exception e) {
			L2DamageTracker.LOGGER.throwing(Level.ERROR, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					L2DamageTracker.LOGGER.throwing(Level.FATAL, e);
				}
			}
		}
	}

	private static PrintStream getStream(Path path) throws IOException {
		File file = path.toFile();
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					throw new IOException("failed to create directory " + file.getParentFile());
				}
			}
			if (!file.createNewFile()) {
				throw new IOException("failed to create file " + file);
			}
		}
		return new PrintStream(file);
	}

	private static String getStackTrace() {
		var trace = new Throwable().getStackTrace();
		for (var e : trace) {
			if (e.getClassName().startsWith("dev.xkmc.l2damagetracker.contents.attack"))
				continue;
			if (e.getClassName().startsWith("net.neoforged.neoforge.event"))
				continue;
			if (e.getClassName().startsWith("net.neoforged.neoforge.common.damagesource"))
				continue;
			return e.toString();
		}
		return "unknown";
	}

}

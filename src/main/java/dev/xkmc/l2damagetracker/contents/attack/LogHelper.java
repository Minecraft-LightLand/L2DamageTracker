package dev.xkmc.l2damagetracker.contents.attack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LogHelper {

	public enum Type {
		ATTACK, HURT
	}

	private record Key(Type type, UUID uuid) {}

	private record Val(long time, UUID uuid, @Nullable CommandSource source) {}

	private static final Map<Key, Val> MAP = new HashMap<>();
	private static final Val NULL = new Val(0, new UUID(0, 0), null);


	public static boolean savePlayerHurt(ServerPlayer player) {
		if (L2DamageTrackerConfig.COMMON.savePlayerHurt.get()) return true;
		return MAP.getOrDefault(new Key(Type.HURT, player.getUUID()), NULL).time() > time(player);
	}

	public static boolean savePlayerAttack(ServerPlayer player) {
		if (L2DamageTrackerConfig.COMMON.savePlayerAttack.get()) return true;
		return MAP.getOrDefault(new Key(Type.ATTACK, player.getUUID()), NULL).time() > time(player);
	}

	private static long time(ServerPlayer player) {
		return player.server.overworld().getGameTime();
	}

	public static boolean saveEntityHurt(LivingEntity entity) {
		if (entity.level() instanceof ServerLevel sl)
			return MAP.getOrDefault(new Key(Type.HURT, entity.getUUID()), NULL).time() > sl.getGameTime();
		return false;
	}

	public static boolean saveEntityAttack(LivingEntity entity) {
		if (entity.level() instanceof ServerLevel sl)
			return MAP.getOrDefault(new Key(Type.ATTACK, entity.getUUID()), NULL).time() > sl.getGameTime();
		return false;
	}

	public static void buildCommand(LiteralArgumentBuilder<CommandSourceStack> base) {
		base.requires(e -> e.hasPermission(2))
				.then(Commands.argument("entities", EntityArgument.entities())
						.then(Commands.literal("attack")
								.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60 * 24))
										.executes(ctx -> onStart(ctx, Type.ATTACK))))
						.then(Commands.literal("hurt")
								.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60 * 24))
										.executes(ctx -> onStart(ctx, Type.HURT)))));
	}

	public static void tick(MinecraftServer server) {
		Multimap<CommandSource, String> removed = HashMultimap.create();
		long gameTime = server.overworld().getGameTime();
		MAP.entrySet().removeIf(ent -> {
			Val val = ent.getValue();
			if (val.time() >= gameTime) return false;
			removed.put(val.source(), val.uuid().toString());
			return true;
		});
		for (var ent : removed.asMap().entrySet()) {
			String pl = ent.getValue().size() == 1 ? new ArrayList<>(ent.getValue()).get(0) : ent.getValue().size() + " entities";
			ent.getKey().sendSystemMessage(Component.literal("Finished damage profiling for " + pl));
		}
	}

	private static int onStart(CommandContext<CommandSourceStack> ctx, Type type) throws CommandSyntaxException {
		int time = ctx.getArgument("time", Integer.class);
		var list = EntityArgument.getEntities(ctx, "entities");
		long expires = ctx.getSource().getServer().overworld().getGameTime() + time;
		for (var e : list) {
			MAP.put(new Key(type, e.getUUID()), new Val(expires, e.getUUID(), ctx.getSource().source));
		}
		int sec = time / 20;
		int min = sec / 60;
		int hrs = min / 60;
		String str = String.format("%02d:%02d:%02d", hrs % 24, min % 60, sec % 60);
		String side = type.name().toLowerCase(Locale.ROOT);
		String pl = list.size() == 1 ? list.iterator().next().getUUID().toString() : list.size() + " entities";
		ctx.getSource().sendSuccess(() -> Component.literal("Start profiling " + side + " of " + pl + " with time " + str), true);
		return 1;
	}

}

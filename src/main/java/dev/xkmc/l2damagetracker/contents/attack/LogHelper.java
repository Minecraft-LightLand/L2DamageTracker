package dev.xkmc.l2damagetracker.contents.attack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LogHelper {

	public enum Type {
		ATTACK, HURT
	}

	private record Key(Type type, UUID uuid) {

	}

	private record Val(long time, String playerName, @Nullable CommandSource source) {

	}

	private static final Map<Key, Val> MAP = new HashMap<>();
	private static final Val NULL = new Val(0, "", null);


	public static boolean savePlayerHurt(ServerPlayer player) {
		if (L2DamageTrackerConfig.SERVER.savePlayerHurt.get()) return true;
		return MAP.getOrDefault(new Key(Type.HURT, player.getUUID()), NULL).time() > time(player);
	}

	public static boolean savePlayerAttack(ServerPlayer player) {
		if (L2DamageTrackerConfig.SERVER.savePlayerAttack.get()) return true;
		return MAP.getOrDefault(new Key(Type.ATTACK, player.getUUID()), NULL).time() > time(player);
	}

	private static long time(ServerPlayer player) {
		return player.server.overworld().getGameTime();
	}

	public static void buildCommand(LiteralArgumentBuilder<CommandSourceStack> base) {
		base.requires(e -> e.hasPermission(2))
				.then(Commands.literal("player")
						.then(argument("player", EntityArgument.players())
								.then(Commands.literal("attack")
										.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60 * 24))
												.executes(ctx -> onStart(ctx, Type.ATTACK))))
								.then(Commands.literal("hurt")
										.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60 * 24))
												.executes(ctx -> onStart(ctx, Type.HURT)))))
				);
	}

	public static void tick(MinecraftServer server) {
		Multimap<CommandSource, String> removed = HashMultimap.create();
		MAP.entrySet().removeIf(ent -> {
			var player = server.getPlayerList().getPlayer(ent.getKey().uuid());
			if (player == null || ent.getValue().time() < time(player)) {
				removed.put(ent.getValue().source(), ent.getValue().playerName());
				return true;
			}
			return false;
		});
		for (var ent : removed.asMap().entrySet()) {
			String pl = ent.getValue().size() == 1 ? new ArrayList<>(ent.getValue()).get(0) : ent.getValue().size() + " players";
			ent.getKey().sendSystemMessage(Component.literal("Finished damage profiling for " + pl));
		}

	}


	private static int onStart(CommandContext<CommandSourceStack> ctx, Type type) throws CommandSyntaxException {
		int time = ctx.getArgument("time", Integer.class);
		EntitySelector sel = ctx.getArgument("player", EntitySelector.class);
		var list = sel.findPlayers(ctx.getSource());
		for (var e : list) {
			MAP.put(new Key(type, e.getUUID()), new Val(e.server.overworld().getGameTime() + time, e.getScoreboardName(), ctx.getSource().source));
		}
		int sec = time / 20;
		int min = sec / 60;
		int hrs = min / 60;
		String str = String.format("%02d:%02d:%02d", hrs % 24, min % 60, sec % 60);
		String side = type.name().toLowerCase(Locale.ROOT);
		String pl = list.size() == 1 ? list.get(0).getScoreboardName() : list.size() + " players";
		ctx.getSource().sendSuccess(() -> Component.literal("Start profiling " + side + " of " + pl + " with time " + str), true);
		return 1;
	}

	protected static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}


}

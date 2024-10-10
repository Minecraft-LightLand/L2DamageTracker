package dev.xkmc.l2damagetracker.contents.logging;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record LoggingTarget(String path, @Nullable ServerPlayer client) {

}

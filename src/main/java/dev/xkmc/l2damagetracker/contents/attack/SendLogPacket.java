package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public record SendLogPacket(String path, ArrayList<String> file) implements SerialPacketBase<SendLogPacket> {

	@Override
	public void handle(Player player) {
		LogEntry.writeToFile(path, file);
	}

}

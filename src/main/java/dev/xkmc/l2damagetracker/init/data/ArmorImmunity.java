package dev.xkmc.l2damagetracker.init.data;

import net.minecraft.world.effect.MobEffect;

import java.util.LinkedHashSet;

public record ArmorImmunity(LinkedHashSet<MobEffect> set, boolean full) {
}

package dev.xkmc.l2damagetracker.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum L2DTLangData {
	ARMOR_IMMUNE_SET("l2damagetracker.tooltip.tool.immune_set", "Full set grants immunity to: ", 0),
	ARMOR_IMMUNE("l2damagetracker.tooltip.tool.immune", "Grants immunity to: ", 0),
	MULT("l2damagetracker.tooltip.mult", "x%s %s", 2);

	private final String key, def;
	private final int arg;


	L2DTLangData(String key, String def, int arg) {
		this.key = key;
		this.def = def;
		this.arg = arg;
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		return Component.translatable(key, args);
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (L2DTLangData lang : L2DTLangData.values()) {
			pvd.add(lang.key, lang.def);
		}
	}

}

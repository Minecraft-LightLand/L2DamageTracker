package dev.xkmc.l2damagetracker.contents.curios;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.CommonHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TotemHelper {

	public static boolean process(LivingEntity self, DamageSource source) {
		for (var slot : TotemHelper.totemSlots(self)) {
			ItemStack holded = slot.stack();
			if (!holded.isEmpty()) {
				Item item = holded.getItem();
				if (!(item instanceof L2Totem totem))
					continue;
				if (!totem.allow(self, holded, source))
					continue;
				if (self instanceof ServerPlayer serverplayer) {
					InteractionHand hand = slot instanceof HandPred pred ? pred.hand() : InteractionHand.OFF_HAND;
					// not checking if event passed or not because the information is wrong anyway
					CommonHooks.onLivingUseTotem(self, source, holded, hand);
					serverplayer.awardStat(Stats.ITEM_USED.get(holded.getItem()), 1);
					CriteriaTriggers.USED_TOTEM.trigger(serverplayer, holded);
				}
				totem.trigger(self, holded, slot, source);
				return true;
			}
		}
		return false;
	}

	public interface TotemSlot extends Consumer<ItemStack> {

		LivingEntity user();

		ItemStack stack();

		default void add(List<TotemSlot> ans) {
			if (stack().getItem() instanceof L2Totem totem && totem.isValid(user(), stack(), this)) {
				ans.add(this);
			}
		}
	}

	public record HandPred(LivingEntity user, ItemStack stack, InteractionHand hand) implements TotemSlot {

		@Override
		public void accept(ItemStack stack) {
			user.setItemInHand(hand, stack);
		}

	}

	public record CurioPred(LivingEntity user, ItemStack stack, String id, int index,
							IDynamicStackHandler handler) implements TotemSlot {

		@Override
		public void accept(ItemStack stack) {
			handler.setStackInSlot(index, stack);
		}

	}

	public static List<TotemSlot> totemSlots(LivingEntity self) {
		List<TotemSlot> ans = new ArrayList<>();
		new HandPred(self, self.getMainHandItem(), InteractionHand.MAIN_HAND).add(ans);
		new HandPred(self, self.getOffhandItem(), InteractionHand.OFF_HAND).add(ans);
		if (ModList.get().isLoaded("curios")) {
			curioTotemSlots(self, ans);
		}
		return ans;
	}

	private static void curioTotemSlots(LivingEntity self, List<TotemSlot> ans) {
		var opt = CuriosApi.getCuriosInventory(self);
		if (opt.isPresent()) {
			var curio = opt.get();
			for (var handler : curio.getCurios().values()) {
				var stacks = handler.getStacks();
				int n = stacks.getSlots();
				for (int i = 0; i < n; i++) {
					ItemStack stack = stacks.getStackInSlot(i);
					new CurioPred(self, stack, handler.getIdentifier(), i, stacks).add(ans);
				}
			}
		}
	}

}

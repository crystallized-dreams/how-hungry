package ru.alexalabai.howhungry.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.alexalabai.howhungry.HowHungry;
import ru.alexalabai.howhungry.config.ModConfig;

@SuppressWarnings("unused")
@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Shadow private int foodLevel;
    @Shadow private int prevFoodLevel;
    @Shadow private float exhaustion;
    @Shadow private float saturationLevel;
    @Shadow private int foodTickTimer;
    @Shadow public abstract void addExhaustion(float exhaustion);

    @Unique private PlayerEntity player$how_hungry=null;

    @Inject(method="update",at=@At("HEAD"),cancellable=true)
    void overhaulHunger(PlayerEntity player, CallbackInfo info) {
        if(!ModConfig.INSTANCE.enabled) return;
        info.cancel();
        player$how_hungry=player;
        if(!ModConfig.INSTANCE.hungerEnabled) return;
        Difficulty difficulty = player.getWorld().getDifficulty();
        prevFoodLevel = this.foodLevel;
        if (exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (saturationLevel > 0.0F) {
                saturationLevel = Math.max(saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                foodLevel = Math.max(foodLevel - 1, 0);
            }
        }

        boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)&&ModConfig.INSTANCE.hungerCanHeal;
        if (bl && saturationLevel > 0.0F && player.canFoodHeal() && foodLevel >= 20) {
            foodTickTimer++;
            if (foodTickTimer >= 10) {
                float f = Math.min(saturationLevel, 6.0F);
                player.heal(f / 6.0F);
                addExhaustion(f);
                foodTickTimer = 0;
            }
        } else if (bl && foodLevel >= ModConfig.INSTANCE.healAfterHunger && player.canFoodHeal()) {
            foodTickTimer++;
            if (foodTickTimer >= 80) {
                player.heal(1.0F);
                addExhaustion(6.0F);
                foodTickTimer = 0;
            }
        } else if (foodLevel <= ModConfig.INSTANCE.damageAfterHunger) {
            foodTickTimer++;
            if (foodTickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    if(ModConfig.INSTANCE.hungerCanDamage) player.damage(player.getDamageSources().starve(), 1.0F);
                }
                foodTickTimer = 0;
            }
        } else {
            foodTickTimer = 0;
        }
    }
    @Inject(method = "eat",at = @At("HEAD"), cancellable = true)
    void eat(Item item, ItemStack stack, CallbackInfo info) {
        if(!ModConfig.INSTANCE.hungerEnabled&&ModConfig.INSTANCE.foodRestoreHealth) {
            if(player$how_hungry==null) return;
            info.cancel();
            if (item.isFood()) {
                FoodComponent foodComponent = item.getFoodComponent();
                player$how_hungry.heal(foodComponent.getHunger());
            }
        }
    }
}

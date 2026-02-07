package ru.alexalabai.howhungry.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.alexalabai.howhungry.config.ModConfig;

@SuppressWarnings("unused")
@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow
    private int foodLevel;

    @Inject(method="update",at=@At("HEAD"),cancellable=true)
    void dontWorkIfNotNeeded(PlayerEntity player, CallbackInfo info) {
        if(ModConfig.INSTANCE.enabled&&!ModConfig.INSTANCE.hungerEnabled) info.cancel();
    }
    @Inject(method="update",at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/PlayerEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),cancellable=true)
    void dontDamage(PlayerEntity player, CallbackInfo info) {
        if(!ModConfig.INSTANCE.enabled) return;
        if(!ModConfig.INSTANCE.hungerCanDamage||!ModConfig.INSTANCE.hungerEnabled) info.cancel();
    }
}

package ru.alexalabai.howhungry.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.alexalabai.howhungry.config.ModConfig;

@SuppressWarnings("unused")
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @ModifyReturnValue(method="canSprint",at=@At("RETURN"))
    private boolean canSprint(boolean original) {
        if(!ModConfig.INSTANCE.enabled) return original;
        return this.hasVehicle() || this.getHungerManager().getFoodLevel() > ModConfig.INSTANCE.disableRunningAfterHunger || this.getAbilities().allowFlying;
    }
}

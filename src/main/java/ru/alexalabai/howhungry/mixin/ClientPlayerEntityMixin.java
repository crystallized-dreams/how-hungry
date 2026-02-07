package ru.alexalabai.howhungry.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.alexalabai.howhungry.HowHungryClient;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @ModifyReturnValue(method="canSprint",at=@At("RETURN"))
    private boolean canSprint(boolean original) {
        if(!HowHungryClient.serverEnabled) return original;
        return this.hasVehicle() || this.getHungerManager().getFoodLevel() > HowHungryClient.serverRunHunger || this.getAbilities().allowFlying;
    }
}

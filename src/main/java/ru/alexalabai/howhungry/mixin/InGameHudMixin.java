package ru.alexalabai.howhungry.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.alexalabai.howhungry.config.ModClientConfig;

@SuppressWarnings("unused")
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @WrapOperation(method="renderStatusBars",
            at=@At(value="INVOKE",
                    target="Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I")
    )
    @SuppressWarnings("unused")
    private int hookRenderStatusBars(InGameHud instance, LivingEntity entity, Operation<Integer> original) { return ModClientConfig.INSTANCE.drawHunger ? original.call(instance, entity) : -1; }
}

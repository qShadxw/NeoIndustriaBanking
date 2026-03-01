package uk.co.tmdavies.nibanking.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour.class)
public interface FilteringBehaviourAccessor {
    @Accessor("count")
    int getCount();

    @Accessor("count")
    void setCount(int count);
}

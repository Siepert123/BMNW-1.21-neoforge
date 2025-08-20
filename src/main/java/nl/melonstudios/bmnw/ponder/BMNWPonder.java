package nl.melonstudios.bmnw.ponder;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BMNWPonder implements PonderPlugin {
    public static void init() {
        PonderIndex.addPlugin(new BMNWPonder());
    }

    @Override
    public String getModId() {
        return "bmnw";
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        BMNWPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        BMNWPonderTags.register(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {

    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {

    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {

    }
}

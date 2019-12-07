package ninjaphenix.expandedstorage;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ExpandedStorageMixinPlugin implements IMixinConfigPlugin
{
    private final HashMap<String, Boolean> conditionalMixins = new HashMap<>();

    public ExpandedStorageMixinPlugin()
    {
        conditionalMixins.put("ninjaphenix.expandedstorage.mixins.ToweletteSupportMixin", FabricLoader.getInstance().isModLoaded("towelette"));
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        final Boolean rv = conditionalMixins.getOrDefault(mixinClassName, Boolean.TRUE);
        System.out.println("DEBUG: Should Mixin Apply - " + mixinClassName + " = " + rv);
        return rv;
    }

    @Override
    public void onLoad(String mixinPackage) { }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    { }
    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}

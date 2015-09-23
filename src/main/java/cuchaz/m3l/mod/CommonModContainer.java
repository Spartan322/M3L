package cuchaz.m3l.mod;

import com.google.common.collect.ImmutableMap;
import cuchaz.m3l.api.versioning.ArtifactData;
import cuchaz.m3l.api.versioning.VersionRange;
import net.minecraftforge.fml.common.MetadataCollection;

import java.io.File;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

/**
 * @author Caellian
 */
public interface CommonModContainer {
    //RELEASE: Clean this up!
    //TODO: Create a custom CommonModContainer project to allow other mod loaders to use this container and support M3L and each other.
    Map<String, String> EMPTY_PROPERTIES = ImmutableMap.of();

    String getModId();

    String getName();

    String getVersion();

    File getSource();

    ModMetadata getMetadata();

    void bindMetadata(MetadataCollection mc);

    void setEnabledState(boolean var1);

    List<ArtifactData> getRequirements();

    List<ArtifactData> getDependencies();

    List<ArtifactData> getDependants();

    String getSortingRules();

//    boolean registerBus(EventBus var1, LoadController var2);

    boolean matches(Object var1);

    Object getMod();

    ArtifactData getProcessedVersion();

    boolean isImmutable();

    String getDisplayVersion();

    VersionRange acceptableMinecraftVersions();

    Certificate getSigningCertificate();

    Map<String, String> getCustomModProperties();

    Class<?> getCustomResourcePackClass();

    Map<String, String> getSharedModDescriptor();

    Disableable canBeDisabled();

    String getGuiClassName();

    List<String> getOwnedPackages();

    boolean shouldLoadInEnvironment();

    enum Disableable {
        YES,
        RESTART,
        NEVER,
        DEPENDENCIES
    }
}

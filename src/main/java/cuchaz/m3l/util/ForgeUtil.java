package cuchaz.m3l.util;


import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import cuchaz.m3l.api.versioning.ArtifactData;
import cuchaz.m3l.api.versioning.InvalidVersionFormatException;
import cuchaz.m3l.api.versioning.Version;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by caellian on 22/09/15.
 */
public class ForgeUtil {
    public static ModContainer forgeModContainer(final cuchaz.m3l.mod.ModContainer ownModContainer) {
        return new ModContainer() {
            @Override
            public String getModId() {
                return ownModContainer.getModId();
            }

            @Override
            public String getName() {
                return ownModContainer.getName();
            }

            @Override
            public String getVersion() {
                return ownModContainer.getVersion();
            }

            @Override
            public File getSource() {
                return ownModContainer.getSource();
            }

            @Override
            public ModMetadata getMetadata() {
                return forgeModMetadata(ownModContainer.getMetadata());
            }

            @Override
            public void bindMetadata(MetadataCollection metadataCollection) {

            }

            @Override
            public void setEnabledState(boolean b) {
                ownModContainer.setEnabledState(b);
            }

            @Override
            public Set<ArtifactVersion> getRequirements() {
                return null;
            }

            @Override
            public List<ArtifactVersion> getDependencies() {
                return null;
            }

            @Override
            public List<ArtifactVersion> getDependants() {
                return null;
            }

            @Override
            public String getSortingRules() {
                return null;
            }

            @Override
            public boolean registerBus(EventBus eventBus, LoadController loadController) {
                return false;
            }

            @Override
            public boolean matches(Object o) {
                return false;
            }

            @Override
            public Object getMod() {
                return null;
            }

            @Override
            public ArtifactVersion getProcessedVersion() {
                return null;
            }

            @Override
            public boolean isImmutable() {
                return false;
            }

            @Override
            public String getDisplayVersion() {
                return null;
            }

            @Override
            public VersionRange acceptableMinecraftVersionRange() {
                return null;
            }

            @Override
            public Certificate getSigningCertificate() {
                return null;
            }

            @Override
            public Map<String, String> getCustomModProperties() {
                return null;
            }

            @Override
            public Class<?> getCustomResourcePackClass() {
                return null;
            }

            @Override
            public Map<String, String> getSharedModDescriptor() {
                return null;
            }

            @Override
            public Disableable canBeDisabled() {
                return null;
            }

            @Override
            public String getGuiClassName() {
                return null;
            }

            @Override
            public List<String> getOwnedPackages() {
                return null;
            }

            @Override
            public boolean shouldLoadInEnvironment() {
                return false;
            }
        };
    }

    public static ModMetadata forgeModMetadata(final cuchaz.m3l.mod.ModMetadata ownMetadata) {
        ModMetadata result = new ModMetadata();
        result.modId = ownMetadata.id;
        result.name = ownMetadata.name;
        result.description = ownMetadata.description;
        result.url = ownMetadata.website;
        result.updateUrl = ownMetadata.updateUrl;
        result.logoFile = ownMetadata.logoFile;
        result.version = ownMetadata.version.toString();
        result.authorList = ownMetadata.authorList;
        result.credits = Joiner.on(", ").join(ownMetadata.credits);
        result.parent = ownMetadata.parentMod.getModId();
        result.screenshots = ownMetadata.screenshots;
        result.useDependencyInformation = ownMetadata.useDependencyInformation;

        ArtifactVersion[] requiredMods = new ArtifactVersion[ownMetadata.requiredMods.size()];
        for (int counter = 0; counter < ownMetadata.requiredMods.size(); counter++) {
            requiredMods[counter] = forgeArtifactData(ownMetadata.requiredMods.get(counter));
        }
        result.requiredMods = Sets.newHashSet(requiredMods);

        ArrayList<ArtifactVersion> dependencies = new ArrayList<ArtifactVersion>(0);
        for (ArtifactData dependency : ownMetadata.dependencies) {
            dependencies.add(forgeArtifactData(dependency));
        }
        result.dependencies = dependencies;

        ArrayList<ArtifactVersion> dependants = new ArrayList<ArtifactVersion>(0);
        for (ArtifactData dependant : ownMetadata.dependants) {
            dependants.add(forgeArtifactData(dependant));
        }
        result.dependencies = dependants;
        result.autogenerated = true;
        return result;
    }

    public static ArtifactVersion forgeArtifactData(final ArtifactData ownArtifactData) {
        return new ArtifactVersion() {
            @Override
            public String getLabel() {
                return ownArtifactData.getLabel();
            }

            @Override
            public String getVersionString() {
                return ownArtifactData.getVersion().toString();
            }

            @Override
            public boolean containsVersion(ArtifactVersion artifactVersion) {
                try {
                    cuchaz.m3l.api.versioning.VersionRange range = new cuchaz.m3l.api.versioning.VersionRange(new Version(getVersionString()));
                    return range.isInRange(new Version(artifactVersion.getVersionString()));
                } catch (InvalidVersionFormatException e) {
                    return false;
                }
            }

            @Override
            public String getRangeString() {
                return null;
            }

            @Override
            public int compareTo(ArtifactVersion o) {
                try {
                    return new Version(getVersionString()).compareTo(new Version(o.getVersionString()));
                } catch (InvalidVersionFormatException e) {
                    return 0;
                }
            }
        };
    }

    public static ArtifactData m3lArtifactVersion(final ArtifactVersion forgeArtifactVersion) {
        try {
            return new ArtifactData(forgeArtifactVersion.getLabel(), Version.fromString(forgeArtifactVersion.getVersionString()));
        } catch (InvalidVersionFormatException e) {
            return new ArtifactData(forgeArtifactVersion.getLabel(), Version.EMPTY);
        }
    }
}

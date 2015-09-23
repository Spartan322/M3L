package cuchaz.m3l.event.state;

import cuchaz.m3l.api.State;
import cuchaz.m3l.lib.Constants;
import cuchaz.m3l.mod.ModContainer;
import cuchaz.m3l.mod.ModMetadata;

import java.io.File;

/**
 * @author Caellian
 */
public class M3LPreInitializationEvent extends M3LStateEvent {
    private ModMetadata modMetadata;
    private File sourceFile;
    private File configurationDir;
    private File suggestedConfigFile;
    private ModContainer modContainer;

    public M3LPreInitializationEvent(Object... data) {
        super(data);
        this.configurationDir = (File) data[0];
    }

    public void applyModContainer(ModContainer activeContainer) {
        this.modContainer = activeContainer;
        this.modMetadata = activeContainer.getMetadata();
        this.sourceFile = activeContainer.getSource();
        this.suggestedConfigFile = new File(this.configurationDir, activeContainer.getModId() + Constants.CONFIGURATION_EXTENSION);
    }

    public ModMetadata getModMetadata() {
        return modMetadata;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public File getConfigurationDir() {
        return configurationDir;
    }

    public File getSuggestedConfigFile() {
        return suggestedConfigFile;
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    @Override
    public State getState() {
        return State.PRE_INITIALIZED;
    }
}

CONTRIBUTING
============

Setting up Minecraft 1.8.3 using build.py
-----------------------------------------
1. Clone the M3L repository using your favorite Mercurial client or run: `hg clone https://bitbucket.org/cuchaz/m3l`
2. Clone the ssjb repostory and install it as Python module by putting the ssjb folder inside the repo to the Python module folder (for more information: https://docs.python.org/2/install/#how-installation-works)
3. Install Minecraft 1.8.3 from the Minecraft launcher if it's not already installed
4. Download Forge 11.14.1.1334 and put it in the lib folder inside the M3L directory (http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.8-11.14.1.1334/forge-1.8-11.14.1.1334-universal.jar)
5. Open a terminal inside the M3L folder
6. Run `python build.py getDeps`
7. Run `python build.py decompMinecraftClient`
8. Open your favorite IDE and create a new project
9. Add the src and conf directories as source folders
10. Add `minecraft-1.8.3-client-deobf.jar`, `minecraft-1.8.3-deps.jar`, `m3l-libs.jar`, `m3l-test-libs.jar` and `m3l-forge-api-1.8.3-0.3b-1334.jar` from the lib directory to the classpath
11. To run the project select `cuchaz.m3l.MainLauncher` as main class and put `client` in the program arguments
12. Add `1.8.3.jar` (from `.minecraft/versions/1.8.3`), `minecraft-1.8.3-deps.jar`, `m3l-libs.jar` and `m3l-test-libs.jar` (from the lib folder) to the launch classpath in that order
13. Make sure it runs inside the `minecraft` folder inside the M3L directory, so none of those files are picked up by Mercurial
14. Create another run configuration with `cuchaz.m3l.MainTranslationIndex` as main class
15. Put `client /path/to/.minecraft/versions/1.8.3/1.8.3.jar lib/minecraft-1.8.3-client-deobf.jar` as program agruments
16. Everytime the mappings change run the translation index (using the configuration just created) and make sure you refresh your IDE project to make sure all the file changes are loaded
17. Create yet another run configuration with `cuchaz.m3l.MainCompileHooks` as main class
18. Put `client lib/minecraft-1.8.3-client-deobf.jar` as program agruments
19. Everytime you add or update a hook run this and make sure you refresh your IDE project to make sure all the file changes are loaded
20. To run mods in the M3L workspace add the mod project to the launch configuration classpath
21. Also add `-Dcuchaz.m3l.modClassNames=<mod class names>` to the VM arguments, for example: `-Dcuchaz.m3l.modClassNames=cubicchunks.TallWorldsMod`
22. To build to project run `python build.py build`, this will throw an error because maven isn't configured, but the result will still be ouputted in the build folder

Setting up any Minecraft version using build.gradle
---------------------------------------------------
1. Clone the M3L repository using your favorite Mercurial client or run: `hg clone https://bitbucket.org/cuchaz/m3l`
2. Open a terminal inside the M3L folder
3. Run `gradle setupDevEnv`
4. If you're using Eclipse, run `gradle eclipse`.
   If you're using IntelliJ IDEA, open a project from existing sources and select `build.gradle` file.
   For any other IDE, create a new project inside of M3L folder and follow the above guide from (inclusive) step 9 forward.
5. Follow `Setting up Minecraft 1.8.3 using build.py` guide from (inclusive) step 11.
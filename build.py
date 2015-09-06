import os
import sys
import json

from urllib.request import *

# settings
PathSsjb = "../ssjb"
MinecraftVersion = "1.8.3"
M3LVersion = "%s-0.3b" % MinecraftVersion
DirNatives = os.path.join("natives", MinecraftVersion)
DirLib = "lib"
DirBin = "bin"
DirBuild = "build"
MappingsCommit = "88962d643ca3912333ede2da1b25d9a1092d5781"
MappingsFile = None #"../Enigma Mappings/1.8.3.mappings"
PathMappings = "conf/%s.client.mappings" % MinecraftVersion
PathLocalMavenRepo = "../maven"
PathForgeUniversal = os.path.join(DirLib, "forge-1.8-11.14.1.1334-universal.jar")
ForgeVersion = "1334"

MinecraftRepo = "https://s3.amazonaws.com/Minecraft.Download/"
VersionURL = "%sversions/%s/" % (MinecraftRepo, MinecraftVersion)

# import ssjb
sys.path.insert(0, PathSsjb)
try:
	import ssjb
	import ssjb.ivy
except:
	print ("Couldn't find ssjb library by cuchaz in parent folder!")
	quit()


# dependencies
ExtraRepos = [
	"http://maven.cuchazinteractive.com",
	"https://libraries.minecraft.net",
]
Deps = [
	ssjb.ivy.Dep("cuchaz:enigma-lib:0.10.4b"), #, localPath="../Enigma/build/enigma-lib-0.10.4b.jar"), # TEMP
	ssjb.ivy.Dep("net.minecraft:launchwrapper:1.8"),
	ssjb.ivy.Dep("org.apache.commons:commons-lang3:3.1"),
	ssjb.ivy.Dep("org.slf4j:slf4j-api:1.7.10"),
	ssjb.ivy.Dep("ch.qos.logback:logback-classic:1.1.2"),
	ssjb.ivy.Dep("org.javassist:javassist:3.19.0-GA")
]
EnigmaStandaloneDep = ssjb.ivy.Dep("cuchaz:enigma:0.10.4b") #, localPath="../Enigma/build/enigma-0.10.4b.jar") # TEMP
TestDeps = [
	ssjb.ivy.Dep("junit:junit:4.12"),
	ssjb.ivy.Dep("org.hamcrest:hamcrest-all:1.3")
]

M3LArtifact = ssjb.ivy.Dep("cuchaz:m3l:%s" % M3LVersion)
ForgeApiArtifact = ssjb.ivy.Dep("cuchaz:m3l-forge-api:%s-%s" % (M3LVersion, ForgeVersion))


# functions

def getOs():
	if sys.platform == "linux" or sys.platform == "linux2":
		return "linux"
	elif sys.platform == "darwin":
		return "osx"
	elif sys.platform == "win32":
		return "windows"

def downloadSilent(url, path="tmp"):
	ownerDir = os.path.dirname(path)
	if not os.path.exists(ownerDir):
		os.makedirs(ownerDir)
	u = urlopen(url)
	with open(path, "wb") as file:
		file.write(u.read())

def downloadWithProgress(url, path="tmp"):
	ownerDir = os.path.dirname(path)
	if not os.path.exists(ownerDir):
		os.makedirs(ownerDir)
	u = urlopen(url)
	f = open(path, 'wb')
	meta = u.info()
	fileSize = int(u.headers['content-length'])
	print ("Downloading: %s Bytes: %s" % (path, fileSize))

	fileSizeDl = 0
	blockSize = 8192
	while True:
		buffer = u.read(blockSize)
		if not buffer:
			break

		fileSizeDl += len(buffer)
		f.write(buffer)
		status = r"%10d  [%3.2f%%]" % (fileSizeDl, fileSizeDl * 100. / fileSize)
		status = status + "\033[F"
		print (status)
	print ("\n\033[F")
	f.close()

def getMinecraftDir():
	myOs = getOs()
	if myOs == "linux":
		return os.path.join(os.path.expanduser("~"), ".minecraft")
	elif myOs == "osx":
		return os.path.join(os.path.expanduser("~"), "Library/Application Support/minecraft")
	elif myOs == "windows":
		return os.path.join(os.path.expanduser("~"), "AppData/Roaming/.minecraft")
	else:
		raise Exception("Don't know how to get minecraft dir on %s yet!" % os)

def getMinecraftJson(version):
	downloadWithProgress("%s%s.json" % (VersionURL, version), "download/%s.json" % version)
	with open("download/%s.json" % version, "r") as file:
		data = json.load(file)
	return data

def getMinecraftClientJar(version):
	downloadWithProgress("%s%s.jar" % (VersionURL, version), "%s/%s/%s.jar" % (DirLib, version, version))
	return "%s/%s/%s.jar" % (DirLib, version, version)
	
def getMinecraftServerJar(version):
	downloadWithProgress("%sminecraft_server.%s.jar" % (VersionURL, version), "%s/%s/minecraft_server.%s.jar" % (DirLib, version, version))
	return "%s/%s/minecraft_server.%s.jar" % (DirLib, version, version)

def getDepPath(dep, natives=None):
	jarName = dep.getName()
	if natives is not None:
		jarName = "%s-%s-%s" % (dep.artifactId, dep.version, natives)
		# resolve any vars in the name
		jarName = jarName.replace("${arch}", "%d" % ssjb.getJvmBitness())

	downloadWithProgress("https://libraries.minecraft.net/%s/%s/%s/%s.jar" % (dep.groupId.replace(".","/"), dep.artifactId, dep.version, jarName), "download/%s.jar" % jarName)
	return "download/%s.jar" % jarName

def isForOs(targetOs, rules):
	requiredOs = None
	forbiddenOs = None
	for rule in rules:
		action = rule["action"]
		if action == "allow":
			if "os" in rule:
				requiredOs = rule["os"]["name"]
		elif action == "disallow":
			if "os" in rule:
				forbiddenOs = rule["os"]["name"]
	return forbiddenOs != targetOs and (requiredOs is None or requiredOs == targetOs)

def getEnigmaPath():
	return os.path.join(DirLib, "%s.jar" % EnigmaStandaloneDep.getName())

def callEnigma(command, args):
	ssjb.callJava(getEnigmaPath(), "cuchaz.enigma.CommandMain", [command] + args)

def makeMinecraftDepsJar(pathOut, dirNatives):
	myOs = getOs()
	with ssjb.file.TempDir("tmp") as dirTemp:

		libs = getMinecraftJson(MinecraftVersion)["libraries"]
		for lib in libs:

			# is this library appropriate for our os?
			if "rules" in lib:
				if not isForOs(myOs, lib["rules"]):
					continue

			dep = ssjb.ivy.Dep(lib["name"])
			if "natives" in lib:
				# put the natives in one place
				ssjb.jar.unpackJar(dirNatives, getDepPath(dep, lib["natives"][myOs]))
			else:
				# and the classes in another
				ssjb.jar.unpackJar(dirTemp, getDepPath(dep))

		# remove any signature files
		for file in ssjb.file.find(dirTemp, "*.RSA"):
			ssjb.file.delete(os.path.join(dirTemp, file))

		ssjb.jar.makeJar(pathOut, dirTemp)

def deobfuscateJar(pathIn, pathOut):
	print ("Deobfuscating %s ..." % pathIn)
	callEnigma("deobfuscate", [pathIn, pathOut, PathMappings])
	print ("Wrote %s" % pathOut)
	
def decompileJar(pathIn, pathOut):
	print ("Decompiling %s ..." % pathIn)
	with ssjb.file.TempDir("tmp") as dirTemp:
		callEnigma("decompile", [pathIn, dirTemp])
		ssjb.jar.makeJar(pathOut, dirTemp)
	print ("Wrote %s" % pathOut)

def publifyJar(pathIn, pathOut):
	print ("Publifying %s ..." % pathIn)
	callEnigma("publify", [pathIn, pathOut])
	print ("Wrote %s" % pathOut)

# TODO: find a better way to represent the whitelist?
ForgeApiWhitelistFiles = set([
	"MinecraftForge-License.txt",
	"MinecraftForge-Credits.txt",
	"CREDITS-fml.txt",
	"LICENSE-fml.txt"
])
ForgeApiWhitelistClasses = set([
	"net.minecraftforge.fml.common.Mod",
	"net.minecraftforge.fml.common.Mod$EventHandler",
	"net.minecraftforge.fml.common.Mod$Instance",
	"net.minecraftforge.fml.common.Mod$CustomProperty",
	"net.minecraftforge.fml.common.LoadController",
	"net.minecraftforge.fml.common.MetadataCollection",
	"net.minecraftforge.fml.common.ModContainer",
	"net.minecraftforge.fml.common.ModContainer$Disableable",
	"net.minecraftforge.fml.common.ModMetadata",
	"net.minecraftforge.fml.common.event.FMLEvent",
	"net.minecraftforge.fml.common.event.FMLStateEvent",
	"net.minecraftforge.fml.common.event.FMLConstructionEvent",
	"net.minecraftforge.fml.common.event.FMLInitializationEvent",
	"net.minecraftforge.fml.common.event.FMLLoadCompleteEvent",
	"net.minecraftforge.fml.common.event.FMLPostInitializationEvent",
	"net.minecraftforge.fml.common.event.FMLPreInitializationEvent",
	"net.minecraftforge.fml.common.eventhandler.IEventListener",
	"net.minecraftforge.fml.common.eventhandler.ListenerList",
	"net.minecraftforge.fml.common.eventhandler.SubscribeEvent",
	"net.minecraftforge.fml.common.eventhandler.Event",
	"net.minecraftforge.fml.common.eventhandler.ASMEventHandler",
	"net.minecraftforge.fml.common.eventhandler.EventPriority",
	"net.minecraftforge.fml.common.versioning.ArtifactVersion",
	"net.minecraftforge.fml.common.versioning.VersionRange"
])

def filenameToClassname(filename):
	if len(filename) <= 6:
		return None
	return filename.replace('/', '.')[0:-6]

def forgeApiWhitelist(info):
	allow = info.filename in ForgeApiWhitelistFiles or filenameToClassname(info.filename) in ForgeApiWhitelistClasses
	if allow:
		print ("\t", info.filename)
	return allow

def makeForgeApiJar(pathOut):
	with ssjb.file.TempDir(os.path.join(DirBuild, "tmp")) as dirTemp:
		ssjb.jar.unpackJar(dirTemp, PathForgeUniversal, forgeApiWhitelist)
		ssjb.jar.makeJar(pathOut, dirTemp)


# tasks

def taskGetDeps():
	ssjb.file.mkdir(DirLib)
	ssjb.ivy.makeJar(getEnigmaPath(), EnigmaStandaloneDep, ExtraRepos)
	ssjb.ivy.makeLibsJar(os.path.join(DirLib, "m3l-libs.jar"), Deps, ExtraRepos)
	ssjb.ivy.makeLibsJar(os.path.join(DirLib, "m3l-test-libs.jar"), TestDeps)
	makeForgeApiJar(os.path.join(DirLib, "%s.jar" % ForgeApiArtifact.getName()))

def taskGetMappings():
	if MappingsFile is not None and os.path.isfile(MappingsFile):
		ssjb.file.cp(MappingsFile, PathMappings)
		print ("Read mappings from %s" % MappingsFile)
	else:
		downloadWithProgress("https://bitbucket.org/cuchaz/minecraft-mappings/raw/%s/%s.mappings" % (MappingsCommit, MinecraftVersion), PathMappings)

def taskDeobfMinecraftClient():
	ssjb.file.mkdir(DirLib)
	ssjb.file.delete(DirNatives)
	ssjb.file.mkdir(DirNatives)
	makeMinecraftDepsJar(os.path.join(DirLib, "minecraft-%s-deps.jar" % MinecraftVersion), DirNatives)
	pathTempJar = os.path.join(DirLib, "minecraft-%s-temp.jar" % MinecraftVersion)
	pathDeobfJar = os.path.join(DirLib, "minecraft-%s-client-deobf.jar" % MinecraftVersion)
	pathSrcJar = os.path.join(DirLib, "minecraft-%s-client-deobf-src.jar" % MinecraftVersion)
	deobfuscateJar(getMinecraftClientJar(MinecraftVersion), pathTempJar)
	publifyJar(pathTempJar, pathDeobfJar)
	decompileJar(pathDeobfJar, pathSrcJar)

def taskBuild():
	ssjb.file.delete(DirBuild)
	ssjb.file.mkdir(DirBuild)
	with ssjb.file.TempDir(os.path.join(DirBuild, "tmp")) as dirTemp:
		ssjb.file.copyTree(dirTemp, DirBin, ssjb.file.find(DirBin))
		ssjb.file.delete(os.path.join(dirTemp, "LICENSE.txt"))
		ssjb.file.copy(dirTemp, "README.txt")
		ssjb.file.copy(dirTemp, "LICENSE.txt")
		pathJar = os.path.join(DirBuild, "%s.jar" % M3LArtifact.getName())
		ssjb.jar.makeJar(pathJar, dirTemp)
		ssjb.ivy.deployJarToLocalMavenRepo(PathLocalMavenRepo, pathJar, M3LArtifact, deps=Deps)
	pathForgeApiJar = os.path.join(DirLib, "%s.jar" % ForgeApiArtifact.getName())
	ssjb.ivy.deployJarToLocalMavenRepo(PathLocalMavenRepo, pathForgeApiJar, ForgeApiArtifact)

def taskInfo():
	print ("Following tasks are available (as arguments):")
	for task in ssjb.tasks:
		if task != "main":
			print(task)

ssjb.registerTask("getDeps", taskGetDeps)
ssjb.registerTask("getMappings", taskGetMappings)
ssjb.registerTask("deobfMinecraftClient", taskDeobfMinecraftClient)
ssjb.registerTask("build", taskBuild)
ssjb.registerTask("main", taskInfo)
ssjb.registerTask("help", taskInfo)
ssjb.run()
package cuchaz.m3l.util;

import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.MethodEntry;
import cuchaz.enigma.mapping.Signature;


public class EntryFactory extends cuchaz.enigma.mapping.EntryFactory {

	public static ClassEntry getClassEntry(Class<?> c) {
		return new ClassEntry(Descriptor.toJvmName(c.getName()));
	}

	public static BehaviorEntry getBehaviorEntry(Class<?> c, String behaviorName, String behaviorSignature, Class<?> ... signatureTypes) {
		Object[] typeNames = new String[signatureTypes.length];
		for (int i=0; i<signatureTypes.length; i++) {
			typeNames[i] = Util.getClassDesc(signatureTypes[i]);
		}
		return getBehaviorEntry(Descriptor.toJvmName(c.getName()), behaviorName, String.format(behaviorSignature, typeNames));
	}

	public static MethodEntry getMethodEntry(ConstPool pool, int methodRefIndex) {
		return new MethodEntry(
			new ClassEntry(Descriptor.toJvmName(pool.getMethodrefClassName(methodRefIndex))),
			pool.getMethodrefName(methodRefIndex),
			new Signature(pool.getMethodrefType(methodRefIndex))
		);
	}
}

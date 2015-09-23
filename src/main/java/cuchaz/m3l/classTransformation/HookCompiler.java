/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import com.google.common.collect.Lists;
import cuchaz.enigma.bytecode.ConstPoolEditor;
import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.EntryFactory;
import cuchaz.enigma.mapping.Type;
import cuchaz.m3l.classTransformation.hooks.*;
import cuchaz.m3l.util.transformation.BytecodeTools;
import javassist.*;
import javassist.bytecode.*;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HookCompiler {

    private List<ClassHook> m_classHooks;
    private List<BehaviorHook> m_behaviorHooks;

    public HookCompiler() {
        m_classHooks = Lists.newArrayList();
        m_behaviorHooks = Lists.newArrayList();
    }

    public Iterable<ClassHook> classHooks() {
        return m_classHooks;
    }

    public Iterable<BehaviorHook> behaviorHooks() {
        return m_behaviorHooks;
    }

    public int getNumHooks() {
        return m_classHooks.size() + m_behaviorHooks.size();
    }

    public void replaceBehavior(CtBehavior behavior)
            throws NotFoundException, CannotCompileException {
        m_behaviorHooks.add(new ReplaceBehaviorHook(behavior, compile(behavior)));
    }

    public void insertBeforeBehavior(CtBehavior behavior, String code)
            throws NotFoundException, CannotCompileException {
        m_behaviorHooks.add(new InsertBeforeBehaviorHook(behavior, compile(behavior, code)));
    }

    public void insertAfterVoidBehavior(CtBehavior behavior, String code)
            throws NotFoundException, CannotCompileException {

        // make sure this is a void method
        if (behavior instanceof CtMethod) {
            if (((CtMethod) behavior).getReturnType() != CtClass.voidType) {
                throw new IllegalArgumentException(behavior.getDeclaringClass().getName() + "." + behavior.getName() + "() was not a void method!");
            }
        }

        m_behaviorHooks.add(new InsertAfterVoidBehaviorHook(behavior, compile(behavior, code)));
    }

    public void insertBeforeReturn(CtBehavior behavior, String code)
            throws NotFoundException, CannotCompileException {
        m_behaviorHooks.add(new InsertBeforeReturnHook(behavior, compile(behavior, code)));
    }

    public void replaceInt(CtBehavior behavior, int val, ClassEntry replacementClassEntry, String replacementMethodName, String args)
            throws NotFoundException, CannotCompileException {
        byte[] bytecode = compileInvokeStaticLoadArgsKeepReturnValue(behavior, replacementClassEntry, replacementMethodName, args);
        m_behaviorHooks.add(new IntReplacementHook(behavior, val, bytecode));
    }

    public void replaceDouble(CtBehavior behavior, double val, ClassEntry replacementClassEntry, String replacementMethodName, String args)
            throws NotFoundException, CannotCompileException {
        byte[] bytecode = compileInvokeStaticLoadArgsKeepReturnValue(behavior, replacementClassEntry, replacementMethodName, args);
        m_behaviorHooks.add(new DoubleReplacementHook(behavior, val, bytecode));
    }

    public void replaceGtZero(CtBehavior behavior, ClassEntry replacementClassEntry, String replacementMethodName, String args)
            throws NotFoundException, CannotCompileException {
        byte[] bytecode = compileInvokeStaticLoadArgsKeepReturnValue(behavior, replacementClassEntry, replacementMethodName, args);
        m_behaviorHooks.add(new GtZeroReplacementHook(behavior, bytecode));
    }

    public void replaceLtZero(CtBehavior behavior, ClassEntry replacementClassEntry, String replacementMethodName, String args)
            throws NotFoundException, CannotCompileException {
        byte[] bytecode = compileInvokeStaticLoadArgsKeepReturnValue(behavior, replacementClassEntry, replacementMethodName, args);
        m_behaviorHooks.add(new LtZeroReplacementHook(behavior, bytecode));
    }

    public void replaceVirtualCall(CtBehavior behavior, BehaviorEntry targetBehaviorCall, ClassEntry replacementClassEntry, String replacementMethodName)
            throws NotFoundException, CannotCompileException {
        replaceVirtualCall(behavior, targetBehaviorCall, replacementClassEntry, replacementMethodName, false);
    }

    public void replaceVirtualCall(CtBehavior behavior, BehaviorEntry targetBehaviorCall, ClassEntry replacementClassEntry, String replacementMethodName, boolean includeArguments)
            throws NotFoundException, CannotCompileException {

        // get the list of arguments we want to pass to the compiler
        List<Type> types = new ArrayList<Type>();
        types.add(new Type(targetBehaviorCall.getClassEntry()));
        for (Type type : targetBehaviorCall.getSignature().getArgumentTypes()) {
            types.add(type);
        }
        if (includeArguments) {
            BehaviorEntry behaviorEntry = EntryFactory.getBehaviorEntry(behavior);
            if (!Modifier.isStatic(behavior.getModifiers())) {
                types.add(new Type(behaviorEntry.getClassEntry()));
            }
            for (Type type : behaviorEntry.getSignature().getArgumentTypes()) {
                types.add(type);
            }
        }

        // create dummy arguments for the compiler
        StringBuilder buf = new StringBuilder();
        for (Type type : types) {
            if (buf.length() > 0) {
                buf.append(", ");
            }

            if (type.isClass()) {
                buf.append("(" + Descriptor.toJavaName(type.getClassEntry().getName()) + ")null");
            } else if (type.isArray()) {
                Type arrayType = type.getArrayType();
                if (arrayType.isClass()) {
                    buf.append("(" + Descriptor.toJavaName(arrayType.getClassEntry().getName()) + "[])null");
                } else if (arrayType.isPrimitive()) {
                    // TODO: get primitive names, not the boxed names
                    // make this will work for now
                    buf.append("(" + arrayType.getPrimitive().name() + "[])null");
                }
            } else if (type.isPrimitive()) {
                if (type.getPrimitive() == Type.Primitive.Character) {
                    buf.append("'0'");
                } else if (type.getPrimitive() == Type.Primitive.Boolean) {
                    buf.append("false");
                } else {
                    buf.append("0");
                }
            }
        }

        byte[] bytecode = compileInvokeStatic(behavior, replacementClassEntry, replacementMethodName, buf.toString());
        m_behaviorHooks.add(new VirtualCallReplacementHook(behavior, bytecode, targetBehaviorCall, includeArguments));
    }

    public void replaceVirtualCallThenArrayAccess(CtBehavior behavior, BehaviorEntry targetBehaviorCall, ClassEntry replacementClassEntry, String replacementMethodName)
            throws NotFoundException, CannotCompileException {
        byte[] bytecode = compileInvokeStatic(behavior, replacementClassEntry, replacementMethodName, "null, 0");
        m_behaviorHooks.add(new VirtualCallThenArrayAccessReplacementHook(behavior, bytecode, targetBehaviorCall));
    }

    private byte[] compile(CtBehavior behavior, String code)
            throws NotFoundException, CannotCompileException {
        try {
            CtClass c = behavior.getDeclaringClass();
            MethodInfo info = behavior.getMethodInfo();
            CodeAttribute attribute = info.getCodeAttribute();

            // make a place to put the bytecode
            Bytecode bytecode = new Bytecode(c.getClassFile().getConstPool(), 0, attribute.getMaxLocals() + 1);
            bytecode.setStackDepth(attribute.getMaxStack() + 1);

            // compile the source code to bytecode
            Javac compiler = new Javac(bytecode, c);
            int numArgs = compiler.recordParams(behavior.getParameterTypes(), Modifier.isStatic(behavior.getModifiers()));
            compiler.recordParamNames(attribute, numArgs);
            compiler.recordLocalVariables(attribute, 0);
            compiler.recordReturnType(Descriptor.getReturnType(info.getDescriptor(), c.getClassPool()), true);
            compiler.compileStmnt(code);

            // save the bytecode
            bytecode = BytecodeTools.copyBytecodeToConstPool(ConstPoolEditor.newConstPool(), bytecode);
            return BytecodeTools.writeBytecode(bytecode);

        } catch (CompileError ex) {
            throw new CannotCompileException(ex);
        } catch (BadBytecode ex) {
            throw new CannotCompileException(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private byte[] compile(CtBehavior behavior)
            throws NotFoundException, CannotCompileException {
        try {
            MethodInfo info = behavior.getMethodInfo();
            CodeAttribute attribute = info.getCodeAttribute();

            // convert the method to bytecode form
            Bytecode bytecode = new Bytecode(info.getConstPool(), attribute.getMaxStack(), attribute.getMaxLocals());
            BytecodeTools.setBytecode(bytecode, attribute.getCode());
            BytecodeTools.setExceptionTable(bytecode, attribute.getExceptionTable());

            bytecode = BytecodeTools.copyBytecodeToConstPool(ConstPoolEditor.newConstPool(), bytecode);
            return BytecodeTools.writeBytecode(bytecode);
        } catch (BadBytecode ex) {
            throw new CannotCompileException(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private byte[] compileInvokeStatic(CtBehavior behavior, ClassEntry classEntry, String accessorName, String args)
            throws NotFoundException, CannotCompileException {
        try {

            // compile the method invocation
            String code = String.format("%s.%s(%s);", Descriptor.toJavaName(classEntry.getName()), accessorName, args);
            byte[] encodedBytecode = compile(behavior, code);

            // pull out the invokestatic from the opcodes
            Bytecode bytecode = BytecodeTools.readBytecode(encodedBytecode);
            Bytecode newBytecode = new Bytecode(bytecode.getConstPool(), bytecode.getMaxStack(), bytecode.getMaxLocals());
            newBytecode.addGap(3);
            CodeIterator iterator = bytecode.toCodeAttribute().iterator();
            while (iterator.hasNext()) {
                int index = iterator.next();
                int opcode = iterator.byteAt(index);
                if (opcode == Opcode.INVOKESTATIC) {

                    // copy the invoke static opcode to the new bytecode
                    newBytecode.write(0, opcode);
                    newBytecode.write(1, iterator.byteAt(index + 1) & 0xff);
                    newBytecode.write(2, iterator.byteAt(index + 2) & 0xff);

                    break;
                }
            }

            return BytecodeTools.writeBytecode(newBytecode);

        } catch (BadBytecode ex) {
            throw new CannotCompileException(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private byte[] compileInvokeStaticLoadArgsKeepReturnValue(CtBehavior behavior, ClassEntry classEntry, String accessorName, String args)
            throws NotFoundException, CannotCompileException {

        // compile the method invocation
        String code = String.format("%s.%s(%s);", Descriptor.toJavaName(classEntry.getName()), accessorName, args);
        byte[] encodedBytecode = compile(behavior, code);

        try {
            // copy everything but the last pop, we want to save the return value
            Bytecode bytecode = BytecodeTools.readBytecode(encodedBytecode);
            Bytecode newBytecode = new Bytecode(bytecode.getConstPool(), bytecode.getMaxStack(), bytecode.getMaxLocals());
            CodeIterator readIterator = bytecode.toCodeAttribute().iterator();

            // find the invoke static pos
            int size = -1;
            while (readIterator.hasNext()) {
                int index = readIterator.next();
                int opcode = readIterator.byteAt(index);
                if (opcode == Opcode.INVOKESTATIC) {
                    size = index + 3; // +3 for the invoke static instruction size
                    break;
                }
            }
            assert (size >= 0);

            // copy the bytecode
            newBytecode.addGap(size); // +3 for the invoke static instruction size
            for (int i = 0; i < size; i++) {
                newBytecode.write(i, bytecode.read(i));
            }

            return BytecodeTools.writeBytecode(newBytecode);

        } catch (BadBytecode ex) {
            throw new CannotCompileException(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    @SuppressWarnings("unused")
    private void dumpOpcodes(Bytecode bytecode)
            throws BadBytecode {
        System.out.println("opcodes");
        CodeIterator iterator = bytecode.toCodeAttribute().iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            int opcode = iterator.byteAt(index);
            System.out.println("\t" + Integer.toHexString(opcode));
        }
    }
}

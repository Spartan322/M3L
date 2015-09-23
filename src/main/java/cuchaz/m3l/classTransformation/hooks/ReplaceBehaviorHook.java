/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import cuchaz.m3l.util.transformation.BytecodeTools;
import javassist.CtBehavior;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Opcode;

public class ReplaceBehaviorHook extends BehaviorHook {

    private static final long serialVersionUID = -2283336296660979801L;

    public ReplaceBehaviorHook(CtBehavior behavior, byte[] bytes) {
        super(behavior, bytes);
    }

    @Override
    protected void onApply(CtBehavior behavior, Bytecode bytecode)
            throws BadBytecode {

        bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);

        CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();

        // make sure there's enough room to write the bytecode
        if (iterator.getCodeLength() < bytecode.getSize()) {
            iterator.appendGap(bytecode.getSize() - iterator.getCodeLength());
        }

        // replace the method with this bytecode
        iterator.write(bytecode.get(), 0);
        iterator.insert(bytecode.getExceptionTable(), 0);

        // fill any remaining space with nops
        for (int i = bytecode.getSize(); i < iterator.getCodeLength(); i++) {
            iterator.writeByte(Opcode.NOP, i);
        }
    }
}

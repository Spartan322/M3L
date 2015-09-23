/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import cuchaz.enigma.mapping.BehaviorEntry;
import cuchaz.enigma.mapping.MethodEntry;
import cuchaz.m3l.util.EntryFactory;
import cuchaz.m3l.util.transformation.BytecodeTools;
import javassist.CtBehavior;
import javassist.bytecode.*;

public class VirtualCallThenArrayAccessReplacementHook extends BehaviorHook {

    private static final long serialVersionUID = -2316695172357710161L;

    private BehaviorEntry m_targetBehaviorCall;

    public VirtualCallThenArrayAccessReplacementHook(CtBehavior behavior, byte[] bytecode, BehaviorEntry targetBehaviorCall) {
        super(behavior, bytecode);

        m_targetBehaviorCall = targetBehaviorCall;
    }

    @Override
    protected void onApply(CtBehavior behavior, Bytecode bytecode)
            throws BadBytecode {

        bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
        byte[] code = bytecode.get();

        ConstPool pool = behavior.getDeclaringClass().getClassFile().getConstPool();

        // look for the invoke virtual instruction
        CodeAttribute codeAttribute = behavior.getMethodInfo().getCodeAttribute();
        CodeIterator iterator = codeAttribute.iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            int opcode = iterator.byteAt(index);
            switch (opcode) {
                case Opcode.INVOKEVIRTUAL:

                    // read the method call info
                    int methodRefIndex = iterator.byteAt(index + 1) << 8 | iterator.byteAt(index + 2);
                    MethodEntry methodCall = EntryFactory.getMethodEntry(pool, methodRefIndex);

                    // is this the right method?
                    if (methodCall.equals(m_targetBehaviorCall)) {

                        // find the next aaload, or bail
                        int aaloadIndex = findNextAaload(codeAttribute, index);
                        if (aaloadIndex < 0) {
                            continue;
                        }

                        // shift all the instructions between the invoke virtual and the aaload up 3 bytes
                        int shiftSize = aaloadIndex - index - 3;
                        for (int i = 0; i < shiftSize; i++) {
                            iterator.writeByte(iterator.byteAt(index + i + 3), index + i);
                        }

                        // write the new virtual call
                        int replacementVirtualIndex = index + shiftSize;
                        iterator.writeByte(code[0], replacementVirtualIndex + 0);
                        iterator.writeByte(code[1], replacementVirtualIndex + 1);
                        iterator.writeByte(code[2], replacementVirtualIndex + 2);

                        // fill in the remaining empty space with a nop
                        assert (replacementVirtualIndex + 3 == aaloadIndex);
                        iterator.writeByte(Opcode.NOP, aaloadIndex);

                        // skip the iterator to the end of our change
                        iterator.move(aaloadIndex);
                    }

                    break;
            }
        }
    }

    private int findNextAaload(CodeAttribute codeAttribute, int startIndex)
            throws BadBytecode {
        CodeIterator iterator = codeAttribute.iterator();
        iterator.move(startIndex);
        while (iterator.hasNext()) {
            int index = iterator.next();
            int opcode = iterator.byteAt(index);
            if (opcode == Opcode.AALOAD) {
                return index;
            }
        }
        return -1;
    }
}

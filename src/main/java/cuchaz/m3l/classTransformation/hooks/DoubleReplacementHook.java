/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation.hooks;

import cuchaz.enigma.bytecode.ConstPoolEditor;
import cuchaz.enigma.bytecode.InfoType;
import cuchaz.m3l.util.transformation.BytecodeTools;
import javassist.CtBehavior;
import javassist.bytecode.*;

public class DoubleReplacementHook extends BehaviorHook {

    private static final long serialVersionUID = -1079931895658437052L;

    private double m_val;

    public DoubleReplacementHook(CtBehavior behavior, double val, byte[] bytecode) {
        super(behavior, bytecode);
        m_val = val;
    }

    @Override
    protected void onApply(CtBehavior behavior, Bytecode bytecode)
            throws BadBytecode {

        bytecode = BytecodeTools.prepareMethodForBytecode(behavior, bytecode);
        ConstPool constPool = behavior.getDeclaringClass().getClassFile().getConstPool();
        ConstPoolEditor editor = new ConstPoolEditor(constPool);

        // loop through the opcodes and insert our snippet on top of the constant load call
        CodeIterator iterator = behavior.getMethodInfo().getCodeAttribute().iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            int opcode = iterator.byteAt(index);
            double oldVal = 0;
            switch (opcode) {
                case Opcode.LDC2_W: {

                    // get the old value
                    int constIndex = iterator.byteAt(index + 1) << 8 | iterator.byteAt(index + 2);
                    if (editor.getItem(constIndex).getType() == InfoType.DoubleInfo) {
                        oldVal = constPool.getDoubleInfo(constIndex);

                        // should we replace this const?
                        if (oldVal == m_val) {

                            // LDC2_w three bytes, invokestatic is at least 3 bytes (with loads), so create space if needed
                            int spaceNeeded = bytecode.getSize() - 3;
                            if (spaceNeeded > 0) {
                                iterator.insertGap(spaceNeeded);
                            }
                            iterator.write(bytecode.get(), index);
                            behavior.getMethodInfo().getCodeAttribute().computeMaxStack();
                        }
                    }
                }
                break;
            }
        }
    }
}

/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.classTransformation;

import cuchaz.m3l.api.CodeAnnotation;
import cuchaz.m3l.classTranslation.ClientOnly;
import cuchaz.m3l.lib.Side;
import cuchaz.m3l.util.Logging;
import javassist.*;
import javassist.bytecode.*;
import javassist.expr.MethodCall;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ClassFilter {

    private static final Logger log = Logging.getLogger();
    private Side m_side;

    public ClassFilter(Side side) {
        m_side = side;
    }

    //TODO: Allow filtering of server only methods!
    // Use single interface with Side object for it.
    public void filter(CtClass c) {

        // do we need to filter anything?
        if (m_side != Side.Server) {
            return;
        }

        // filter behaviors
        for (CtBehavior behavior : c.getDeclaredBehaviors()) {
            try {
                if (behavior.getAnnotation(ClientOnly.class) != null) {
                    // remove the behavior
                    if (behavior instanceof CtMethod) {
                        c.removeMethod((CtMethod) behavior);
                    } else if (behavior instanceof CtConstructor) {
                        c.removeConstructor((CtConstructor) behavior);
                    }
                } else {
                    // does this method have code?
                    MethodInfo info = behavior.getMethodInfo();
                    CodeAttribute attribute = info.getCodeAttribute();
                    if (attribute != null) {
                        removeAnnotatedBlocks(attribute.iterator(), c, info);
                    }
                }
            } catch (ClassNotFoundException ex) {
                log.error("Unable to remove behavior " + behavior.getName(), ex);
            } catch (NotFoundException ex) {
                log.error("Unable to remove behavior " + behavior.getName(), ex);
            }
        }

        // filter fields
        for (CtField field : c.getDeclaredFields()) {
            try {
                if (field.getAnnotation(ClientOnly.class) != null) {
                    c.removeField(field);
                }
            } catch (ClassNotFoundException ex) {
                log.error("Unable to remove field " + field.getName(), ex);
            } catch (NotFoundException ex) {
                log.error("Unable to remove field " + field.getName(), ex);
            }
        }
    }

    private void removeAnnotatedBlocks(CodeIterator i, CtClass c, MethodInfo info) {
        List<CodeRange> ranges = new ArrayList<CodeRange>();

        // look for annotated blocks in the behavior
        Integer startIndex = null;
        while (i.hasNext()) {
            try {
                int index = i.next();
                int op = i.byteAt(index);

                // look for the start annotation
                if (op == Opcode.INVOKESTATIC) {
                    MethodCall call = new MethodCallWrapper(index, i, c, info);
                    if (call.getClassName().equals(CodeAnnotation.class.getName()) && call.getMethodName().equals("startClientOnly")) {
                        // sanity checking
                        if (startIndex != null) {
                            throw new IllegalArgumentException("Encountered start block annotation, but another block was already open! " + c.getName() + "." + info.getName() + "()");
                        }

                        startIndex = index;
                    } else if (call.getClassName().equals(CodeAnnotation.class.getName()) && call.getMethodName().equals("stopClientOnly")) {
                        // sanity checking
                        if (startIndex == null) {
                            throw new IllegalArgumentException("Encountered stop block annotation, but no block was open! " + c.getName() + "." + info.getName() + "()");
                        }

                        // get the stop index
                        // it should point to the last byte used by this
                        // instruction
                        // invokestatic ops always use two more bytes
                        int stopIndex = index + 2;

                        ranges.add(new CodeRange(startIndex, stopIndex));
                        startIndex = null;
                    }
                }
            } catch (BadBytecode ex) {
                // not sure what to do about this... just pass it along
                throw new Error(ex);
            }
        }

        // sanity checking. make sure the block was closed
        if (startIndex != null) {
            throw new IllegalArgumentException("Encountered start block annotation, but did not find close block annotation! " + c.getName() + "." + info.getName() + "()");
        }

        // remove the blocks
        for (CodeRange range : ranges) {
            range.remove(i);
        }
    }

    private static class MethodCallWrapper extends MethodCall {
        // all this class does is expose a protected constructor
        protected MethodCallWrapper(int index, CodeIterator iter, CtClass c, MethodInfo info) {
            super(index, iter, c, info);
        }
    }

    private static class CodeRange {
        private int startIndex;
        private int stopIndex;

        public CodeRange(int startIndex, int stopIndex) {
            this.startIndex = startIndex;
            this.stopIndex = stopIndex;
        }

        public void remove(CodeIterator iter) {
            // replace these instructions with NOPs
            // NOTE: javassist doesn't support instruction removal
            // we'll have to make do with NOPs, even though they're not the most
            // efficient way to do this
            for (int i = startIndex; i <= stopIndex; i++) {
                iter.writeByte(Opcode.NOP, i);
            }
        }
    }
}

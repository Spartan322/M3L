/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
package cuchaz.m3l.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import cuchaz.m3l.M3L;
import net.minecraft.util.RegistryNamespaced;

import java.io.*;
import java.util.*;

public class IdNumberMappings {

    // TODO: make this work with the new registries

    private BiMap<Integer, String> m_numberToId;

    public IdNumberMappings() {
        m_numberToId = HashBiMap.create();
    }

    @SuppressWarnings("unchecked")
    public void loadFromRegistry(RegistryNamespaced registry) {
        for (String id : (Set<String>) registry.getRegistryKeys()) {
            int number = registry.getIndexOf(registry.get(id));
            m_numberToId.put(number, id);
        }
    }

    public void write(OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(m_numberToId.size());
        for (Map.Entry<Integer, String> entry : m_numberToId.entrySet()) {
            dout.writeShort(entry.getKey());
            dout.writeUTF(entry.getValue());
        }
    }

    public void read(InputStream in) throws IOException {
        m_numberToId.clear();

        DataInputStream din = new DataInputStream(in);
        int numEntries = din.readInt();
        for (int i = 0; i < numEntries; i++) {
            int number = din.readShort();
            String id = din.readUTF();
            m_numberToId.put(number, id);
        }
    }

    public String getId(int number) {
        return m_numberToId.get(number);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof IdNumberMappings) {
            return equals((IdNumberMappings) other);
        }
        return false;
    }

    public boolean equals(IdNumberMappings other) {
        return m_numberToId.equals(other.m_numberToId);
    }

    public void restoreRegistry(RegistryNamespaced registry) {

        // make a copy of the object ids
        Set<String> ids = new HashSet<String>(m_numberToId.values());
        for (String id : ids) {
            int savedNumber = m_numberToId.inverse().get(id);

            // ignore the zero entry
            if (savedNumber == 0) {
                continue;
            }

            // does this object exist in the registry?
            Object obj = registry.get(id);
            int currentNumber = registry.getIndexOf(obj);
            if (obj == null || currentNumber <= 0) {
                // the object isn't in the registry
                // we can't add it because we have no way to get the object
                // instance
                // we have to ignore it and move on
                M3L.LOGGER.warn("Unable to restore number for " + id + ". This object will not be available in the registry.");
                continue;
            }

            // did the number change?
            if (currentNumber == savedNumber) {
                continue;
            }

            // swap the saved object with its usurper
            Object usurperObj = registry.getObjectAtIndex(savedNumber);
            String usurperId = null; // TODO registry.getNameForObject(usurperObj);
            registry.set(savedNumber, id, obj);
            registry.set(currentNumber, usurperId, usurperObj);

            M3L.LOGGER.info("Restored " + id + " to number " + savedNumber + " and displaced " + usurperId + " to number " + currentNumber);
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        // get all the numbers and sort them
        List<Integer> numbers = Lists.newArrayList(m_numberToId.keySet());
        Collections.sort(numbers);

        // print out the number -> id pairs
        for (Integer number : numbers) {
            String id = m_numberToId.get(number);
            buf.append(String.format("%4d   %s\n", number, id));
        }

        return buf.toString();
    }
}

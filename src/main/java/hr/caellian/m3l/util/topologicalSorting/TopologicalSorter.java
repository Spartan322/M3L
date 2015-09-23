/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package hr.caellian.m3l.util.topologicalSorting;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cuchaz.m3l.M3L;
import cuchaz.m3l.api.Priority;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Caellian
 */
public class TopologicalSorter<N> extends ArrayList<N> {

    protected HashBiMap<Integer, N> itemReferences = HashBiMap.create();
    protected HashMultimap<Integer, Integer> dependencies = HashMultimap.create();
    protected ArrayList<N> sorted = Lists.newArrayList();
    protected HashMap<N, Priority> priorityMap = Maps.newHashMap();
    protected ArrayList<N> beingSorted = Lists.newArrayList();

    @Override
    public boolean add(N n) {
        return this.add(n, null);
    }

    public boolean add(N n, Priority priority) {
        if (n.getClass() != null) {
            this.add(n);
            priorityMap.put(n, priority == null ? Priority.NORMAL : priority);
            int hashCode = n.hashCode();

            itemReferences.put(hashCode, n);

            AnnotatedElement element = (AnnotatedElement) n;
            if (element.isAnnotationPresent(HashDependency.class)) {
                for (int after : element.getDeclaredAnnotation(HashDependency.class).executeAfter()) {
                    if (after != 0) {
                        dependencies.put(hashCode, after);
                    }
                }

                for (int before : element.getDeclaredAnnotation(HashDependency.class).executeBefore()) {
                    if (before != 0) {
                        dependencies.put(before, hashCode);
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        dependencies.removeAll(itemReferences.inverse().get(o));
        itemReferences.inverse().remove(o);
        priorityMap.remove(o);
        return super.remove(o);
    }

    @Override
    public void clear() {
        for (N n : this) {
            remove(n);
        }
    }

    public ArrayList<N> getHandledItem(N item) throws TopologicalCycleException {
        ArrayList<N> result = new ArrayList<N>();

        try {
            if (!beingSorted.contains(item)) {
                int itemHashCode = itemReferences.inverse().get(item);

                beingSorted.add(item);

                if (dependencies.containsKey(itemHashCode)) {
                    for (int depID : dependencies.get(itemHashCode)) {
                        if (depID != itemHashCode) {
                            try {
                                result.addAll(getHandledItem(item));
                            } catch (TopologicalCycleException e) {
                                M3L.LOGGER.error("Failed to sort item '{}' of '{}', it has already been sorted or is in a cycle", item.toString(), item.getClass());
                            }
                        }
                    }
                }
                if (!sorted.contains(item)) {
                    result.add(item);
                    sorted.add(item);
                    beingSorted.remove(item);
                }
            } else {
                throw new TopologicalCycleException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<N> getSortedList() {
        ArrayList<N> result = new ArrayList<N>();

        for (Priority priority : Priority.values()) {
            for (N item : this) {
                if (priorityMap.get(item) == priority) {
                    try {
                        result.addAll(getHandledItem(item));
                    } catch (TopologicalCycleException e) {
                        M3L.LOGGER.error("Failed to sort item '{}' of '{}', it has already been sorted or is in a cycle", item.toString(), item.getClass());
                    }
                }
            }
        }
        sorted.clear();
        return result;
    }

}

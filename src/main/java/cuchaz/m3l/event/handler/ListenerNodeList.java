/*******************************************************************************
 * Copyright (c) 2015 Contributors.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

package cuchaz.m3l.event.handler;

import com.google.common.collect.ImmutableList;
import cuchaz.m3l.api.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Caellian
 */
public class ListenerNodeList {
    private static ImmutableList<ListenerNodeList> listTracker = ImmutableList.of();
    private static int maxSize = 0;

    private Optional<ListenerNodeList> parent;
    private ListenerNode[] lists = new ListenerNode[0];

    public ListenerNodeList() {
        this.parent = Optional.empty();
        addToTracker(this);
        resizeLists(maxSize);
    }

    public ListenerNodeList(ListenerNodeList parent) {
        this.parent = Optional.of(parent);
        addToTracker(this);
        resizeLists(maxSize);
    }

    private synchronized static void addToTracker(ListenerNodeList inst) {
        ImmutableList.Builder<ListenerNodeList> builder = ImmutableList.builder();
        builder.addAll(listTracker);
        builder.add(inst);
        listTracker = builder.build();
    }

    public static void resize(int max) {
        if (max > maxSize) {
            for (ListenerNodeList list : listTracker) {
                list.resizeLists(max);
            }
            maxSize = max;
        }
    }

    public static void clearBusID(int id) {
        for (ListenerNodeList list : listTracker) {
            list.lists[id].dispose();
        }
    }

    public static void unregisterAll(int id, IEventListener listener) {
        for (ListenerNodeList list : listTracker) {
            list.unregister(id, listener);
        }
    }

    public void resizeLists(int max) {
        if (parent.isPresent() && parent != Optional.<ListenerNodeList>empty()) {
            parent.get().resizeLists(max);
        }

        if (lists.length < max) {
            ListenerNode[] newList = new ListenerNode[max];
            int x = 0;
            while (x < lists.length) {
                newList[x] = lists[x++];
            }
            while (x < max) {
                if (parent.isPresent() && parent != Optional.<ListenerNodeList>empty()) {
                    newList[x] = new ListenerNode(parent.get().getInstance(x));
                } else {
                    newList[x++] = new ListenerNode();
                }
            }
            lists = newList;
        }
    }

    protected ListenerNode getInstance(int id) {
        return lists[id];
    }

    public IEventListener[] getListeners(int id) {
        return lists[id].getListeners();
    }

    public void register(int id, Priority priority, IEventListener listener) {
        lists[id].register(priority, listener);
    }

    public void unregister(int id, IEventListener listener) {
        lists[id].unregister(listener);
    }

    private class ListenerNode {
        private boolean rebuild = true;
        private Optional<IEventListener[]> listeners;
        private ArrayList<ArrayList<IEventListener>> priorities;
        private Optional<ListenerNode> parent;

        ListenerNode(ListenerNode parent) {
            this();
            this.parent = Optional.of(parent);
        }

        ListenerNode() {
            int count = Priority.values().length;
            priorities = new ArrayList<ArrayList<IEventListener>>(count);

            for (int cntr = 0; cntr < count; cntr++) {
                priorities.add(new ArrayList<IEventListener>(0));
            }
        }

        public void dispose() {
            for (ArrayList<IEventListener> listeners : priorities) {
                listeners.clear();
            }
            priorities.clear();
            parent = Optional.empty();
            listeners = Optional.empty();
        }

        /**
         * Returns a ArrayList containing all listeners for this event,
         * and all parent events for the specified priority.
         * <p/>
         * The list is returned with the listeners for the children events first.
         *
         * @param priority The Priority to get
         * @return ArrayList containing listeners
         */
        public ArrayList<IEventListener> getListeners(Priority priority) {
            ArrayList<IEventListener> ret = new ArrayList<IEventListener>(priorities.get(priority.ordinal()));
            if (parent.isPresent() && parent != Optional.<ListenerNode>empty()) {
                ret.addAll(parent.get().getListeners(priority));
            }
            return ret;
        }

        /**
         * Returns a full list of all listeners for all priority levels.
         * Including all parent listeners.
         * <p/>
         * List is returned in proper priority order.
         * <p/>
         * Automatically rebuilds the internal Array cache if its information is out of date.
         *
         * @return Array containing listeners
         */
        public IEventListener[] getListeners() {
            if (shouldRebuild()) {
                buildCache();
            }
            return listeners.get();
        }

        protected boolean shouldRebuild() {
            return rebuild || (parent.isPresent() && parent.get().shouldRebuild());
        }

        /**
         * Rebuild the local Array of listeners, returns early if there is no work to do.
         */
        private void buildCache() {
            if (parent.isPresent() && parent != Optional.<ListenerNode>empty() && parent.get().shouldRebuild()) {
                parent.get().buildCache();
            }

            ArrayList<IEventListener> ret = new ArrayList<IEventListener>(0);
            for (final Priority value : Priority.values()) {
                List<IEventListener> listeners = getListeners(value);
                if (listeners.size() > 0) {
                    ret.add(new IEventListener() {
                        @Override
                        public void elicit(M3LEvent event) {
                            event.setPriority(value);
                        }
                    });
                    ret.addAll(listeners);
                }
            }
            listeners = Optional.of(ret.toArray(new IEventListener[ret.size()]));
            rebuild = false;
        }

        public void register(Priority priority, IEventListener listener) {
            priorities.get(priority.ordinal()).add(listener);
            rebuild = true;
        }

        public void unregister(IEventListener listener) {
            for (ArrayList<IEventListener> list : priorities) {
                if (list.remove(listener)) {
                    rebuild = true;
                }
            }
        }
    }
}

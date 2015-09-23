package hr.caellian.m3l.util.topologicalSorting;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import cuchaz.m3l.api.Priority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Caellian
 */
public class OrderedMethodExecutor extends TopologicalSorter<Method> {
    protected HashBiMap<String, Method> methodReferences = HashBiMap.create();
    protected HashMultimap<String, String> dependencies = HashMultimap.create();

    @Override
    public boolean add(Method n) {
        return add(n, null);
    }

    public boolean add(Method n, Priority priority) {
        super.add(n, priority);

        String formattedName = String.format("%s:%s", n.getDeclaringClass().getSimpleName(), n.getName());

        methodReferences.put(formattedName, n);

        if (n.isAnnotationPresent(Dependency.class)) {
            for (String after : n.getDeclaredAnnotation(Dependency.class).executeAfter()) {
                if (!after.isEmpty()) {
                    dependencies.put(formattedName, after);
                }
            }

            for (String before : n.getDeclaredAnnotation(Dependency.class).executeBefore()) {
                if (!before.isEmpty()) {
                    dependencies.put(before, formattedName);
                }
            }
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        dependencies.removeAll(methodReferences.inverse().get(o));
        methodReferences.inverse().remove(o);
        return super.remove(o);
    }

    @Override
    public void clear() {
        for (Method method : this) {
            remove(method);
        }
        super.clear();
    }

    public boolean execute(Map<Method, Object> referenceMap, Object... args) {

        ArrayList<Method> toExecute = this.getSortedList();

        for (Method method : toExecute) {
            try {
                Boolean accessibleBefore = method.isAccessible();
                method.setAccessible(true);
                method.invoke(referenceMap.get(method), args);
                method.setAccessible(accessibleBefore);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean execute(Object obj, Object... args) {
        ArrayList<Method> toExecute = this.getSortedList();

        for (Method method : toExecute) {
            try {
                Boolean accessibleBefore = method.isAccessible();
                method.setAccessible(true);
                method.invoke(obj, args);
                method.setAccessible(accessibleBefore);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}

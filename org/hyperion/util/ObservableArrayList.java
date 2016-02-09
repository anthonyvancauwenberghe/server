package org.hyperion.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ObservableArrayList<T> extends ArrayList<T> {


    public interface Listener<T> {

        void onChange(final ObservableArrayList<T> list);
    }

    private final Set<Listener<T>> listeners = new HashSet<>();

    public ObservableArrayList<T> listen(final Listener<T> l){
        listeners.add(l);
        return this;
    }

    @Override
    public T set(int index, T element) {
        super.set(index, element);
        fireChanged();
        return element;
    }

    @Override
    public boolean add(final T obj) {
        if(!super.add(obj))
            return false;
        fireChanged();
        return true;
    }

    @Override
    public boolean remove(final Object obj) {
        if(!super.remove(obj))
            return false;
        if(obj != null)
            fireChanged();
        return true;
    }

    protected void fireChanged(){
        listeners.forEach(l -> l.onChange(this));
    }
}

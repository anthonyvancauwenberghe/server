package org.hyperion.rs2.util;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A class which represents a list of entities.
 *
 * @param <E> The type of entity.
 * @author Graham Edgecombe
 */
public class EntityList<E extends Entity> implements Collection<E>, Iterable<E> {

	/**
	 * Internal entities array.
	 */
	private Entity[] entities;

	/**
	 * Current size.
	 */
	private int size = 0;

	/**
	 * Creates an entity list with the specified capacity.
	 *
	 * @param capacity The capacity.
	 */
	public EntityList(int capacity) {
		entities = new Entity[capacity + 1]; // do not use idx 0
	}

	public Entity get(int index) {
		if(index <= 0 || index >= entities.length) {
			throw new IndexOutOfBoundsException("Index : " + index);
		}
		return entities[index];
	}

	/**
	 * Gets the index of an entity.
	 *
	 * @param entity The entity.
	 * @return The index in the list.
	 */
	public int indexOf(Entity entity) {
		return entity.getIndex();
	}

	/**
	 * Gets the next free id.
	 *
	 * @return The next free id.
	 */
	private int getNextId() {
		for(int i = 1; i < entities.length; i++) {
			if(entities[i] == null) {
				return i;
			}
		}
		return - 1;
	}

	@Override
	public boolean add(E arg0) {
		int id = getNextId();
		if(id == - 1) {
			return false;
		}
		entities[id] = arg0;
		arg0.setIndex(id);
		size++;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		boolean changed = false;
		for(E entity : arg0) {
			if(add(entity)) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public void clear() {
		for(int i = 1; i < entities.length; i++) {
			entities[i] = null;
		}
		size = 0;
	}

	/*@Override
	public boolean contains(Object arg0) {
		for(int i = 1; i < entities.length; i++) {
			if(entities[i] == arg0) {
				return true;
			}
		}
		return false;
	}*/

	@Override
	public boolean contains(Object arg0) {
		Entity entity = (Entity) arg0;
		return entities[entity.getIndex()] == entity;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		boolean failed = false;
		for(Object o : arg0) {
			if(! contains(o)) {
				failed = true;
			}
		}
		return ! failed;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<E> iterator() {
		return new EntityListIterator<E>(this);
	}

	@Override
	public boolean remove(Object arg0) {
		for(int i = 1; i < entities.length; i++) {
			if(entities[i] == arg0) {
				entities[i] = null;
				size--;
				if(arg0 instanceof Player) {
					World.updatePlayersOnline();
					World.updateStaffOnline();
				}
				return true;
			}
		}
		return false;
	}

	public Optional<E> search(Predicate<E> filter) {
		for (Entity e : entities) {
			if (e == null)
				continue;
			if (filter.test((E)e))
				return Optional.of((E)e);
		}
		return Optional.empty();
	}

	@Override
	public boolean removeAll(Collection<?> arg0){
		boolean changed = false;
		for(Object o : arg0) {
			if(remove(o)) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		boolean changed = false;
		for(int i = 1; i < entities.length; i++) {
			if(entities[i] != null) {
				if(! arg0.contains(entities[i])) {
					entities[i] = null;
					size--;
					changed = true;
				}
			}
		}
		return changed;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Entity[] toArray() {
		int size = size();
		Entity[] array = new Entity[size];
		int ptr = 0;
		for(int i = 1; i < entities.length; i++) {
			if(entities[i] != null) {
				try {
					array[ptr++] = entities[i];
				} catch(ArrayIndexOutOfBoundsException e) {
					return array;
				}
			}
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] arg0) {
		Entity[] arr = toArray();
		return (T[]) Arrays.copyOf(arr, arr.length, arg0.getClass());
	}

}

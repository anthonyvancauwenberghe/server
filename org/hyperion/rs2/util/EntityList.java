package org.hyperion.rs2.util;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.EntityHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

import java.util.*;
import java.util.function.Predicate;

/**
 * A class which represents a list of entities.
 * Rewritten partially for faster processing
 *
 * @param <E> The type of entity.
 * @author Graham Edgecombe
 * @author Gilles
 */
public class EntityList<E extends Entity> implements Collection<E>, Iterable<E> {

	/**
	 * Internal entities array.
	 */
	public E[] entities;

	/**
	 * The queue containing all of the slots that were
	 * recently removed from. This is used to reduce slot lookup times for
	 * characters being added to this character list.
	 */
	private final Queue<Integer> slotQueue = new LinkedList<>();

	/**
	 * The finite capacity of this collection.
	 */
	private final int capacity;

	/**
	 * Current size.
	 */
	private int size = 0;

	/**
	 * Creates an entity list with the specified capacity.
	 *
	 * @param capacity The capacity.
	 */
	@SuppressWarnings("unchecked")
	public EntityList(int capacity) {
		this.capacity = ++capacity;
		entities = (E[])new Entity[this.capacity]; // do not use idx 0
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

	@Override
	public boolean add(E e) {
		Objects.requireNonNull(e);

		if (!e.isRegistered()) {
			int slot = slotSearch();
			if (slot < 0)
				return false;
			e.setRegistered(true);
			e.setIndex(slot);
			entities[slot] = e;
			size++;
			if(e instanceof Player) {
				World.updatePlayersOnline();
				World.updateStaffOnline();
			}
			return true;
		}
		return false;
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

	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		Objects.requireNonNull(o);
		if(!(o instanceof Entity))
			return false;
		E e = (E)o;
		return entities[e.getIndex()] != null;
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
		return new EntityListIterator<>(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(Object arg0) {
		Objects.requireNonNull(arg0);
		if(!(arg0 instanceof Entity))
			return false;
		E e = (E)arg0;

		if (e instanceof Player) {
			Player player = (Player) e;
			if (player.getSession().isConnected()) {
				player.getSession().close(true);
			}
		}

		if (e.isRegistered() && entities[e.getIndex()] != null) {
			e.setRegistered(false);
			entities[e.getIndex()] = null;
			slotQueue.add(e.getIndex());
			size--;
			if(e instanceof Player) {
				World.updatePlayersOnline();
				World.updateStaffOnline();
			}
			return true;
		}
		return false;
	}

	public Optional<E> search(Predicate<? super E> filter) {
		for (E e : entities) {
			if (e == null)
				continue;
			if (filter.test(e))
				return Optional.of(e);
		}
		return Optional.empty();
	}

	private int slotSearch() {
		if (slotQueue.size() == 0) {
			for (int slot = 1; slot < capacity; slot++) {
				if (entities[slot] == null) {
					return slot;
				}
			}
			return -1;
		}
		return slotQueue.remove();
	}

	public boolean remove(Player player, boolean force) {
		if(force) {
			for(int i = 1; i < entities.length; i++) {
				if(entities[i] == player) {
					System.out.println("Successfully terminated player");
					entities[i] = null;
					size--;
					return true;
				}
			}
		} else {
			System.out.println("Attempting to deregister player instead");
			EntityHandler.deregister(player);
		}
		return false;
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

	public int capacity() {
		return capacity;
	}
}

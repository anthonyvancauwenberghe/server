package org.hyperion;

import org.hyperion.rs2.net.Packet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * The PacketQueue is a a datastructure with an underlying array
 * which is built so that if there is an element with index S that is null,
 * every element with index higher than S will be null aswell.
 *
 * @author Arsen Maxyutov.
 */
public class PacketQueue implements Queue<Packet> {

	/**
	 * The maximum packets queue size.
	 */
	public static final int MAX_SIZE = 200;

	/**
	 * The packets.
	 */
	private Packet[] packets;

	/**
	 * The cursor.
	 */
	private int cursor = 0;

	/**
	 * Constructs a new Packet Queue.
	 */
	public PacketQueue() {
		packets = new Packet[MAX_SIZE];
	}

	/**
	 * Adds a new packet to the packet queue.
	 *
	 * @param packet
	 */
	public boolean add(Packet packet) {
		if(cursor + 1 >= MAX_SIZE)
			return false;
		packets[cursor++] = packet;
		return true;
	}

	/**
	 * Sets the specified packet index to null.
	 *
	 * @param index
	 */
	public void remove(int index) {
		packets[index] = null;
	}

	/**
	 * Gets the packet with the specified index.
	 *
	 * @param index
	 * @return the packet with the given index.
	 */
	public Packet get(int index) {
		return packets[index];
	}

	/**
	 * Sets the cursor.
	 *
	 * @param counter
	 */
	public void setCursor(int counter) {
		this.cursor = counter;
	}

	@Override
	public boolean addAll(Collection<? extends Packet> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Packet> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Packet element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean offer(Packet e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Packet peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Packet poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Packet remove() {
		// TODO Auto-generated method stub
		return null;
	}
}

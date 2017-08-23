package Genetics;

import java.util.ArrayList;
import java.util.List;


public class Table {

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public List<Integer> getGuests() {
		return guests;
	}

	public void setGuests(List<Integer> guests) {
		this.guests = guests;
	}

	private int capacity;
	private List<Integer> guests = new ArrayList<>();

	public boolean isFull() {
		return guests.size() == capacity;
	}

	public boolean isEmpty() {
		return guests.size() == 0;
	}
}

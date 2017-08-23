package Genetics;

import java.util.ArrayList;
import java.util.List;

public class Solution {

	private int score;

	public Solution(int score) {
		this.score = 0;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	List<Table> tables = new ArrayList<>();

	public void computeScore(Constraints constraints) {
		this.score = 0;
		for (Table table : tables) {
			for (Integer guest : table.getGuests()) {
				for (Integer otherGuest : table.getGuests()) {
					if (guest != otherGuest) {
						if (constraints.getSameTable().get(guest - 1).contains(otherGuest)) {
							this.score += 1;
						}
						if (constraints.getNotSameTable().get(guest - 1).contains(otherGuest)) {
							this.score -= 1;
						}
					}
				}

			}
		}
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	public void print() {
		System.out.println("Best Solution : ");
		int j = 1;
		for (Table table : tables) {
			System.out.print("Table" + j++ + ": ");
			for (Integer i : table.getGuests()) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
		System.out.println("Score: " + score);
	}
}

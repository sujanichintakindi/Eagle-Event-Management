package Genetics;

import java.util.ArrayList;
import java.util.List;

public class Constraints {
	private List<List<Integer>> sameTable = new ArrayList<>();
	private List<List<Integer>> notSameTable = new ArrayList<>();
	public List<List<Integer>> getSameTable() {
		return sameTable;
	}
	public void setSameTable(List<List<Integer>> sameTable) {
		this.sameTable = sameTable;
	}
	public List<List<Integer>> getNotSameTable() {
		return notSameTable;
	}
	public void setNotSameTable(List<List<Integer>> notSameTable) {
		this.notSameTable = notSameTable;
	}
}
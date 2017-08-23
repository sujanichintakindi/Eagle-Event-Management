package Genetics;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import Models.Event;
import Models.EventGuests;
import Models.EventRules;

public class ParentGeneration {

	private List<Solution> parentGeneration = new ArrayList<>();
	private Solution bestSolution;

	public Solution getBestSolution() {
		return bestSolution;
	}

	public ParentGeneration(Event event) {
		bestSolution = new Solution(-100000);
		Constraints constraints = new Constraints();
		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
		EntityManager entitymanager = emfactory.createEntityManager();
		entitymanager.getTransaction().begin();
		Query query = entitymanager.createQuery("SELECT e"+" FROM EventRules e WHERE e.event.eventId=:eventId");
		query.setParameter("eventId", event.getEventId());
		@SuppressWarnings("unchecked")
		List<EventRules> list = (List<EventRules>)query.getResultList();
		Query query2 = entitymanager.createQuery("SELECT g"+" FROM EventGuests g WHERE g.event.eventId=:eventId");
		query2.setParameter("eventId", event.getEventId());
		@SuppressWarnings("unchecked")
		List<EventGuests> guests = (List<EventGuests>)query2.getResultList();
		// same table = [[],[],[]]
		for (int i = 0; i < guests.size(); i++) {
			constraints.getSameTable().add(new ArrayList<>());
			constraints.getNotSameTable().add(new ArrayList<>());
		}
		for(EventRules rule : list) {
			if(rule.getScore() == 1) {
				constraints.getSameTable().get(rule.getMainGuest().getEventGuestNumber()-1).add(rule.getSubGuest().getEventGuestNumber());
			}
			else if(rule.getScore() == -1) {
				constraints.getNotSameTable().get(rule.getMainGuest().getEventGuestNumber()-1).add(rule.getSubGuest().getEventGuestNumber());
			}
		}
		entitymanager.close();
		emfactory.close();
		//[ [] , [] ,[] , [] ]
		for(int i=1; i <= constraints.getSameTable().size(); i++){
			Solution solution = generateParent(constraints, i, event.getnSeats());
			if(solution.getScore() > bestSolution.getScore()) {
				bestSolution = solution;
			}
			parentGeneration.add(solution);
		}
	}

	public Solution generateParent(Constraints constraints, int start, int capacity) {
		Solution solution = new Solution(0);
		//[3,.....,n,1,2]
		//divident = divisor*quotient+reminder 2/10 , 2 =(10*0)+2
		List<Integer> guests = new ArrayList<>();
		int size = constraints.getSameTable().size();
		start--;
		for (int i = 0; i < size; i++) {
			start %= size; // 2%10 = 2, 3%10 = 3, 8%10=8, 9%10=9,10%10=0,1
			start++; // 3, 4, ... ,9,10,1,2
			guests.add(start); //3, 4, .. 9,10,1,2
		}
		//T1 - [7,...10,1,2. ]  [3,4]  , [5,6]
		while (guests.size() != 0) {
			int currentGuest = guests.get(0);
			List<Integer> sameTable2 = new ArrayList<Integer>(constraints.getSameTable().get(currentGuest-1));
			List<Integer> sameTable = new ArrayList<Integer>();
			for(Integer n : sameTable2) {
				if(guests.contains(n)) {
					sameTable.add(n);
				}
			}
			sameTable.add(currentGuest);
			for (int i = 0; i < constraints.getSameTable().size(); i++) {
				if (i != currentGuest - 1) {
					if (constraints.getSameTable().get(i).contains(currentGuest) && !sameTable.contains(i + 1)) {
						sameTable.add(i + 1);
					}
				}
			}
			Table table = new Table();
			int size1 = sameTable.size();
			table.setCapacity(capacity);
			for (int j = 0; j < size1 ; j++) {
				if (!table.isFull()) {
					table.getGuests().add(sameTable.get(j));
					guests.remove(sameTable.get(j));
				}
			}
			solution.getTables().add(table);
		}
		solution.computeScore(constraints);
		return solution;
	}
}
package Models;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int resultId;
	@ManyToOne
	@JoinColumn(name = "EVENT_EVENTID")
	private Event event;
	@NotNull
	private int tableNumber;
	@ManyToOne
	@JoinColumn(name = "GUEST_GUESTID")
	private EventGuests guest;
	public int getResultId() {
		return resultId;
	}
	public void setResultId(int outputId) {
		this.resultId = outputId;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public int getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	public EventGuests getGuest() {
		return guest;
	}
	public void setGuest(EventGuests guest) {
		this.guest = guest;
	}
}
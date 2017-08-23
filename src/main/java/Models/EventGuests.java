package Models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
public class EventGuests implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int guestId;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private Integer eventGuestNumber;
	@ManyToOne
	@JoinColumn(name = "EVENT_EVENTID")
	private Event event;
	@OneToMany(mappedBy ="mainGuest",orphanRemoval=true, cascade={CascadeType.ALL})
	@CascadeOnDelete
    private List<EventRules> mainRules;
	@OneToMany(mappedBy ="subGuest",orphanRemoval=true, cascade={CascadeType.ALL})
	@CascadeOnDelete
    private List<EventRules> subRules;
	@OneToMany(mappedBy ="guest",orphanRemoval=true, cascade={CascadeType.ALL})
	@CascadeOnDelete
    private List<Result> resultData;
	public int getGuestId() {
		return guestId;
	}
	public void setGuestId(int guestId) {
		this.guestId = guestId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getEventGuestNumber() {
		return eventGuestNumber;
	}
	public void setEventGuestNumber(Integer eventGuestNumber) {
		this.eventGuestNumber = eventGuestNumber;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
}
package Models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class EventRules implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ruleId;
	@ManyToOne
	@JoinColumn(name = "MAINGUEST_GUESTID")
	private EventGuests mainGuest;
	@ManyToOne
	@JoinColumn(name = "SUBGUEST_GUESTID")
	private EventGuests subGuest;
	@ManyToOne
	@JoinColumn(name = "EVENT_EVENTID")
	private Event event;
	@NotNull
	private Integer score;
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public int getRuleId() {
		return ruleId;
	}
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}
	public EventGuests getMainGuest() {
		return mainGuest;
	}
	public void setMainGuest(EventGuests mainGuest) {
		this.mainGuest = mainGuest;
	}
	public EventGuests getSubGuest() {
		return subGuest;
	}
	public void setSubGuest(EventGuests subGuest) {
		this.subGuest = subGuest;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
}
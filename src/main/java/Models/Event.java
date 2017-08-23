package Models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int eventId;
	@NotNull
	private String eventName;
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;
	@NotNull
	private String venue;
	@NotNull
	private int nSeats;
	@NotNull
	private int customerId;
	@NotNull
	private String fileLocation;
	@NotNull
	private Integer output;
	public Integer getOutput() {
		return output;
	}
	public void setOutput(Integer output) {
		this.output = output;
	}
	public List<EventGuests> getEventGuests() {
		return eventGuests;
	}
	public void setEventGuests(List<EventGuests> eventGuests) {
		this.eventGuests = eventGuests;
	}
	@OneToMany(mappedBy ="event",orphanRemoval=true, cascade={CascadeType.ALL})
	@CascadeOnDelete
    private List<EventGuests> eventGuests;
	@OneToMany(mappedBy ="event",orphanRemoval=true, cascade={CascadeType.ALL})
	@CascadeOnDelete
    private List<EventRules> eventRules;
	@OneToMany(mappedBy ="event",orphanRemoval=true, cascade={CascadeType.ALL})
	@CascadeOnDelete
    private List<Result> eventResults;
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public int getnSeats() {
		return nSeats;
	}
	public void setnSeats(int nSeats) {
		this.nSeats = nSeats;
	}
	public Event() {
		super();
	}
}
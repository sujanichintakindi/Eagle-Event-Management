package RestApiManagers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import Genetics.ParentGeneration;
import Genetics.Solution;
import Genetics.Table;
import Models.Event;
import Models.EventGuests;
import Models.EventRules;
import Models.Result;

@Path("/EventManagement")
public class EventManager extends Application {
	@GET
	@Path("/FileDownload")
	@Produces("application/csv")
	public Response GetFile(@QueryParam("filePath") String filePath) {
		File file = new File(filePath);
		ResponseBuilder responseBuilder = Response.ok((Object) file);
		responseBuilder.header("Content-Disposition", "attachment; filename=\"EventRules.csv\"");
		return responseBuilder.build();
	}
	//this endpoint accepts files in input
	@SuppressWarnings("resource")
	@POST
	@Path("/AddEvent")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response AddEvent(@FormDataParam("eventName") String eventName, @FormDataParam("datetime") String datetime,
			@FormDataParam("venue") String venue, @FormDataParam("tableSize") Integer tableSize,
			@FormDataParam("customerId") Integer customerId,
			@FormDataParam("importGuestList") InputStream uploadedInputStream,
			@FormDataParam("importGuestList") FormDataContentDisposition fileDetail,
			@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			if (eventName == null || customerId == null || datetime == null || venue == null || tableSize == null
					|| "".equals(venue) || "".equals(datetime) || "".equals(eventName)) {
				return Response.status(200).entity("Please Enter all details to process the request").build();
			}
			Event event = new Event();
			event.setEventName(eventName);
			event.setnSeats(tableSize);
			event.setVenue(venue);
			event.setCustomerId(customerId);
			event.setOutput(0);
			DateFormat formatter = new SimpleDateFormat("yyy-MM-dd'T'HH:mm");
			Date eventDateTime = null;
			try {
				eventDateTime = formatter.parse(datetime);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			event.setDateTime(eventDateTime);
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				entitymanager.getTransaction().begin();
				String uploadedFileLocation = "C:/EEMFileUploads/" + fileDetail.getFileName();
				File file = new File(uploadedFileLocation);
				int num = 0;
				while (file.exists()) {
					uploadedFileLocation = "C:/EEMFileUploads/" + getFileName(fileDetail.getFileName()) + (num++)
							+ getFileExtension(fileDetail.getFileName());
					file = new File(uploadedFileLocation);
				}
				writeToFile(uploadedInputStream, uploadedFileLocation);
				event.setFileLocation(uploadedFileLocation);
				String line = "";
				BufferedReader br = new BufferedReader(new FileReader(uploadedFileLocation));
				int i = 0;
				String[] headers = null;
				try {
					while ((line = br.readLine()) != null) {
						if (i == 0) {
							headers = line.split(",");
							if ( !(headers[0].equals("Guest #") && headers[1].equals("First Name")
									&& headers[2].equals("Last Name") && headers[3].equals("Same Table")) ) {
								entitymanager.close();
								emfactory.close();
								return Response.status(200).entity("Invalid File Input").build();
							}
							i++;
						}
						else {
							entitymanager.persist(event);
							String[] data = line.split(",");
							EventGuests guest = new EventGuests();
							guest.setEvent(event);
							guest.setEventGuestNumber(Integer.valueOf(data[0]));
							guest.setFirstName(data[1]);
							guest.setLastName(data[2]);	
							entitymanager.persist(guest);
						}
					}
				} catch (Exception e) {
					entitymanager.close();
					emfactory.close();
					System.out.println(e);
					return Response.status(200).entity("Invalid File Input").build();
				}
				entitymanager.getTransaction().commit();
				entitymanager.getTransaction().begin();
				br = new BufferedReader(new FileReader(uploadedFileLocation));
				i = 0;
				headers = null;
				try {
					while ((line = br.readLine()) != null) {
						if (i == 0) {
							headers = line.split(",");
							if ( !(headers[0].equals("Guest #") && headers[1].equals("First Name")
									&& headers[2].equals("Last Name") && headers[3].equals("Same Table")) ) {
								entitymanager.close();
								emfactory.close();
								return Response.status(200).entity("Invalid File Input").build();
							}
							i++;
						}
						else {
							String[] data = line.split(",");
							for (int j = 3; j < data.length; j++) {
								if(headers[j].equals("Same Table") && !isBlank(data[j])) {
									System.out.println(data);
									int score = 1;
									EventRules rule = new EventRules();
									rule.setScore(score);
									rule.getMainGuest();
									Query query = entitymanager.createQuery("SELECT e"+" FROM EventGuests e WHERE e.event.eventId=:eventId and e.eventGuestNumber=:eventGuestNumber");
									query.setParameter("eventId", event.getEventId());
									query.setParameter("eventGuestNumber", Integer.valueOf(data[0]));
									EventGuests mainGuest = (EventGuests)query.getResultList().get(0);
									query = entitymanager.createQuery("SELECT e"+" FROM EventGuests e WHERE e.event.eventId=:eventId and e.eventGuestNumber=:eventGuestNumber");
									query.setParameter("eventId", event.getEventId());
									query.setParameter("eventGuestNumber", Integer.valueOf(data[j]));
									EventGuests subGuest = (EventGuests)query.getResultList().get(0);
									rule.setMainGuest(mainGuest);
									rule.setSubGuest(subGuest);
									rule.setEvent(event);
									entitymanager.persist(rule);
								} 
								else if(headers[j].equals("Not Same Table") && !isBlank(data[j])) {
									System.out.println(data);
									int score = -1;
									EventRules rule = new EventRules();
									rule.setScore(score);
									rule.getMainGuest();
									Query query = entitymanager.createQuery("SELECT e"+" FROM EventGuests e WHERE e.event.eventId=:eventId and e.eventGuestNumber=:eventGuestNumber");
									query.setParameter("eventId", event.getEventId());
									query.setParameter("eventGuestNumber", Integer.valueOf(data[0]));
									EventGuests mainGuest = (EventGuests)query.getResultList().get(0);
									query = entitymanager.createQuery("SELECT e"+" FROM EventGuests e WHERE e.event.eventId=:eventId and e.eventGuestNumber=:eventGuestNumber");
									query.setParameter("eventId", event.getEventId());
									query.setParameter("eventGuestNumber", Integer.valueOf(data[j]));
									EventGuests subGuest = (EventGuests)query.getResultList().get(0);
									rule.setMainGuest(mainGuest);
									rule.setSubGuest(subGuest);
									rule.setEvent(event);
									entitymanager.persist(rule);
								}
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e);
					entitymanager.close();
					emfactory.close();
					return Response.status(200).entity("Invalid File Input").build();
				}
				entitymanager.getTransaction().commit();
				entitymanager.getTransaction().begin();
				ParentGeneration p = new ParentGeneration(event);
				Solution output = p.getBestSolution();
				int nTable = 1;
				for(Table table : output.getTables()) {
					for(Integer guestOrder : table.getGuests()) {
						Result result = new Result();
						result.setEvent(event);
						result.setTableNumber(nTable);
						Query query = entitymanager.createQuery("SELECT e"+" FROM EventGuests e WHERE e.event.eventId=:eventId and e.eventGuestNumber=:eventGuestNumber");
						query.setParameter("eventId", event.getEventId());
						query.setParameter("eventGuestNumber", Integer.valueOf(guestOrder));
						EventGuests guest = (EventGuests) query.getResultList().get(0);
						result.setGuest(guest);
						entitymanager.persist(result);
					}
					nTable++;
				}
				event.setOutput(output.getScore());
				entitymanager.persist(event);
				entitymanager.getTransaction().commit();
				entitymanager.close();
				emfactory.close();
				return Response.status(200).entity("success").build();
			} catch (Exception e) {
				System.out.println(e);
				return Response.status(200).entity("Unable to add Event").build();
			}
		}
	}
	@SuppressWarnings("unchecked")
	@POST
	@Path("/GetEvents")
	@Produces(MediaType.TEXT_PLAIN)
	public Response GetEvents(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				entitymanager.getTransaction().begin();
				Query query = entitymanager.createQuery("SELECT e" + " FROM Event e");
				List<Event> list = (List<Event>) query.getResultList();
				int i = 0;
				JSONObject output = new JSONObject();
				Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (Event event : list) {
					JSONObject obj = new JSONObject();
					obj.put("eventId", String.valueOf(event.getEventId()));
					obj.put("customerId", String.valueOf(event.getCustomerId()));
					obj.put("eventName", event.getEventName());
					obj.put("tableSize", String.valueOf(event.getnSeats()));
					obj.put("venue", event.getVenue());
					obj.put("fileLocation", event.getFileLocation());
					if (event.getDateTime() == null) {
						obj.put("eventSchedule", "");
					} else {
						String dateTimeString = formatter.format(event.getDateTime());
						obj.put("eventSchedule", dateTimeString);
					}
					output.put(String.valueOf(i), obj);
					i++;
				}
				entitymanager.close();
				emfactory.close();
				return Response.status(200).entity(output.toString()).build();
			} catch (Exception e) {
				return Response.status(200).entity("Unable to fetch Event Details").build();
			}
		}
	}

	@POST
	@Path("/DeleteEvent")
	@Produces(MediaType.TEXT_PLAIN)
	public Response DeleteEvent(@FormParam("eventId") Integer eventId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			if (!loginUserRole.equals("Admin")) {
				return Response.status(200).entity("This user doesnt have access for this operation").build();
			} else {
				try {
					EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
					EntityManager entitymanager = emfactory.createEntityManager();
					entitymanager.getTransaction().begin();
					Event event = entitymanager.find(Event.class, Integer.valueOf(eventId));
					entitymanager.remove(event);
					entitymanager.getTransaction().commit();
					entitymanager.close();
					emfactory.close();
					return Response.status(200).entity("success").build();
				} catch (Exception e) {
					return Response.status(200).entity("Unable to Delete Event Data").build();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/GetResult")
	@Produces(MediaType.TEXT_PLAIN)
	public Response GetResult(@FormParam("eventId") Integer eventId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				entitymanager.getTransaction().begin();
				Query query = entitymanager.createQuery("SELECT r"+" FROM Result r WHERE r.event.eventId=:eventId");
				query.setParameter("eventId", eventId);
				List<Result> resultSet = (List<Result>) query.getResultList();
				JSONObject output = new JSONObject();
				int i = 0;
				for (Result result : resultSet) {
					JSONObject obj = new JSONObject();
					obj.put("tableNumber", result.getTableNumber());
					obj.put("role", result.getGuest().getFirstName()+" "+result.getGuest().getLastName());
					output.put(String.valueOf(i),obj);
					i++;
				}
				entitymanager.close();
				emfactory.close();
				return Response.status(200).entity(output.toString()).build();
			} catch (Exception e) {
				return Response.status(200).entity("Unable to Delete Event Data").build();
			}
		}
	}

	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {

				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFileName(String name) {
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			name = name.substring(0, pos);
		}
		return name;
	}

	public String getFileExtension(String name) {
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
			name = name.substring(pos, name.length());
		}
		return name;
	}
	
	public static boolean isBlank(String s){
	    return (s == null) || (s.trim().length() == 0);
	}
}
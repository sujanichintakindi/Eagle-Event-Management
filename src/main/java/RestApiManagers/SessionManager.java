package RestApiManagers;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;  
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import Models.Employee;

@Path("/SessionManagement")
public class SessionManager {
	@POST
	@Path("/EmployeeLogin")
	@Produces(MediaType.TEXT_PLAIN)
	public Response EmployeeLogin(@FormParam("password") String password, @FormParam("username") String username, @Context HttpServletRequest request) {
		if(username == null || "".equals(username) || password == null || "".equals(password)) {
			return Response.status(200).entity("Invalid Entires").build();
		} else {
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				Query query = entitymanager.createQuery("SELECT e"+" FROM Employee e WHERE e.username=:username and e.password=:password");
				query.setParameter("username", username);
				query.setParameter("password", password);
				@SuppressWarnings("unchecked")
				List<Employee> list = (List<Employee>)query.getResultList();
				boolean check = false;
				for(Employee e : list) {
					HttpSession session=request.getSession();
					session.setAttribute("employeeId", String.valueOf(e.getEmployeeId()));
					session.setAttribute("role", e.getRole());
					check = true;
				}
				if(check) {
					return Response.status(200).entity("success").build();
				} else {
					return Response.status(200).entity("Invalid Login Credentials").build();
				}
			}
			catch (Exception e) {
				return Response.status(200).entity("Connection Error").build();
			}
		}
	}
	@POST
	@Path("/EmployeeLogout")
	@Produces(MediaType.TEXT_PLAIN)
	public Response EmployeeLogout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			session.invalidate();
			return Response.status(200).entity("Logged Out").build();
		}
	}
	@POST
	@Path("/CheckAdmin")
	@Produces(MediaType.TEXT_PLAIN)
	public Response CheckAdmin() {
		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
		EntityManager entitymanager = emfactory.createEntityManager();
		entitymanager.getTransaction().begin();
		Query query = entitymanager.createQuery("SELECT e"+" FROM Employee e WHERE e.role='Admin'");
		@SuppressWarnings("unchecked")
		List<Employee> list = (List<Employee>)query.getResultList();
		if(list.size() == 1) {
			return Response.status(200).entity("success").build();
		} else{
			Employee employee = new Employee();
			employee.setFirstname("Admin");
			employee.setLastname("Admin");
			employee.setPassword("admin");
			employee.setUsername("admin");
			employee.setRole("Admin");
			try {
				entitymanager.persist(employee);
				entitymanager.getTransaction().commit();
				return Response.status(200).entity("success").build();
			}
			catch (Exception e) {
				return Response.status(200).entity("Unable to Create Admin").build();
			}
			finally {
				entitymanager.close();
				emfactory.close();
			}
		}
	}
	@SuppressWarnings("unchecked")
	@POST
	@Path("/LoginProfileDetails")
	@Produces(MediaType.TEXT_PLAIN)
	public Response LoginProfileDetails(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			try {
				String employeeId = (String) session.getAttribute("employeeId");
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				entitymanager.getTransaction().begin();
				Employee e = entitymanager.find(Employee.class,Integer.valueOf(employeeId));
				JSONObject output = new JSONObject();
				output.put("username", e.getUsername());
				output.put("password", e.getPassword());
				output.put("role", e.getRole());
				output.put("firstname", e.getFirstname());
				output.put("lastname", e.getLastname());
				output.put("id", String.valueOf(e.getEmployeeId()));
				entitymanager.getTransaction().commit();
				entitymanager.close();
				emfactory.close();
				return Response.status(200).entity(output.toString()).build();
			}catch (Exception e) {
				return Response.status(200).entity("Unable to get login details").build();
			}
		}
	}
}
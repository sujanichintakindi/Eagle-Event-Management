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

import Models.Customer;

@Path("/CustomerManagement")
public class CustomerManager {
	
	@POST
	@Path("/AddCustomer")
	@Produces(MediaType.TEXT_PLAIN)
	public Response AddCustomer(@FormParam("name") String name, @FormParam("contact") String contact, @FormParam("email") String email, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			if(name == null || contact == null || email == null || "".equals(email) || "".equals(contact) || "".equals(name)) {
				return Response.status(200).entity("Please Enter all detaiils to process the request").build();
			}
			Customer customer = new Customer();
			customer.setContact(contact);
			customer.setEmail(email);
			customer.setName(name);
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				entitymanager.getTransaction().begin();
				entitymanager.persist(customer);
				entitymanager.getTransaction().commit();
				entitymanager.close();
				emfactory.close();
				return Response.status(200).entity("success").build();
			}
			catch (Exception e) {
				return Response.status(200).entity("Username Already Exists").build();
			}
		}
	}
	@POST
	@Path("/UpdateCustomer")
	@Produces(MediaType.TEXT_PLAIN)
	public Response UpdateCustomer(@FormParam("name") String name, @FormParam("contact") String contact, @FormParam("email") String email, @FormParam("customerId") String customerId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			if(customerId == null || "".equals(customerId) || name == null || contact == null || email == null || "".equals(email) || "".equals(contact) || "".equals(name)) {
				return Response.status(200).entity("Please Enter All Details to Update Customer").build();
			}else {
				try {
					EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
					EntityManager entitymanager = emfactory.createEntityManager();
					entitymanager.getTransaction().begin();
					Customer customer = entitymanager.find(Customer.class,Integer.valueOf(customerId));
					customer.setContact(contact);
					customer.setName(name);
					customer.setEmail(email);
					entitymanager.getTransaction().commit();
					entitymanager.close();
					emfactory.close();
					return Response.status(200).entity("success").build();
				}
				catch (Exception e) {
					return Response.status(200).entity("Unable to Update Customer").build();
				}
			}
		}
	}
	@POST
	@Path("/DeleteCustomer")
	@Produces(MediaType.TEXT_PLAIN)
	public Response DeleteCustomer(@FormParam("customerId") String customerId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			if(customerId == null || "".equals(customerId)) {
				return Response.status(200).entity("Please Enter Customer ID to Delete").build();
			}else {
				try {
					EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
					EntityManager entitymanager = emfactory.createEntityManager();
					entitymanager.getTransaction().begin();
					Customer customer = entitymanager.find(Customer.class,Integer.valueOf(customerId));
					entitymanager.remove(customer);
					entitymanager.getTransaction().commit();
					entitymanager.close();
					emfactory.close();
					return Response.status(200).entity("success").build();
				}
				catch (Exception e) {
					return Response.status(200).entity("Unable to Delete Customer").build();
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	@POST
	@Path("/GetCustomers")
	@Produces(MediaType.TEXT_PLAIN)
	public Response GetCustomers(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
				EntityManager entitymanager = emfactory.createEntityManager();
				entitymanager.getTransaction().begin();
				Query query = entitymanager.createQuery("SELECT c"+" FROM Customer c");
				List<Customer> list = (List<Customer>)query.getResultList();
				int i =0;
				JSONObject output = new JSONObject();
				for (Customer customer : list) {
					JSONObject obj = new JSONObject();
					obj.put("customerId", customer.getCustomerId());
					obj.put("name", customer.getName());
					obj.put("email", customer.getEmail());
					obj.put("contact", customer.getContact());
					output.put(String.valueOf(i),obj);
					i++;
				}
				entitymanager.close();
				emfactory.close();
				return Response.status(200).entity(output.toString()).build();
			}
			catch (Exception e) {
				return Response.status(200).entity("Unable to fetch Customer Details").build();
			}
		}
	}
}
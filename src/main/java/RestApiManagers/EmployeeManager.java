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

@Path("/EmployeeManagement")
public class EmployeeManager {
	@POST
	@Path("/AddEmployee")
	@Produces(MediaType.TEXT_PLAIN)
	public Response AddEmployee(@FormParam("firstname") String firstname, @FormParam("lastname") String lastname, @FormParam("password") String password, @FormParam("username") String username, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			if(!loginUserRole.equals("Admin")) {
				return Response.status(200).entity("This user doesnt have access for this operation").build();
			}
			else {
				if(firstname == null || lastname == null || password == null || username == null || "".equals(username) || "".equals(password) || "".equals(firstname) || "".equals(lastname)) {
					return Response.status(200).entity("Please Enter all details to process the request").build();
				} else {
					Employee employee = new Employee();
					employee.setFirstname(firstname);
					employee.setLastname(lastname);
					employee.setPassword(password);
					employee.setUsername(username);
					employee.setRole("Manager");
					try {
						EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
						EntityManager entitymanager = emfactory.createEntityManager();
						entitymanager.getTransaction().begin();
						entitymanager.persist(employee);
						entitymanager.getTransaction().commit();
						entitymanager.close();
						emfactory.close();
						return Response.status(200).entity("success").build();
					}
					catch (Exception e) {
						return Response.status(200).entity("Unable to Add Employee").build();
					}
				}
			}
		}
	}
	@POST
	@Path("/UpdateEmployee")
	@Produces(MediaType.TEXT_PLAIN)
	public Response UpdateEmployee(@FormParam("firstname") String firstname, @FormParam("lastname") String lastname, @FormParam("employeeId") String employeeId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			String loginEmployeeId = (String) session.getAttribute("employeeId");
			if( !(loginUserRole.equals("Admin") || loginEmployeeId.equals(employeeId)) ) {
				return Response.status(200).entity("This user doesnt have access to perform this operation").build();
			}
			else {
				if(firstname == null || lastname == null || "".equals(firstname) || "".equals(lastname)) {
					return Response.status(200).entity("Please Enter all details to process the request").build();
				} else {
					try {
						EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
						EntityManager entitymanager = emfactory.createEntityManager();
						entitymanager.getTransaction().begin();
						Employee employee = entitymanager.find(Employee.class,Integer.valueOf(employeeId));
						employee.setFirstname(firstname);
						employee.setLastname(lastname);
						entitymanager.getTransaction().commit();
						entitymanager.close();
						emfactory.close();
						return Response.status(200).entity("success").build();
					}
					catch (Exception e) {
						return Response.status(200).entity("Unable to Update Employee").build();
					}
				}
			}
		}
	}
	@POST
	@Path("/DeleteEmployee")
	@Produces(MediaType.TEXT_PLAIN)
	public Response DeleteEmployee(@FormParam("employeeId") String employeeId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			if(!loginUserRole.equals("Admin")) {
				return Response.status(200).entity("This user doesnt have access for this operation").build();
			}
			else {
				if(employeeId == null || "".equals(employeeId)) {
					return Response.status(200).entity("Please Enter Customer ID to Delete").build();
				}else {
					try {
						EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
						EntityManager entitymanager = emfactory.createEntityManager();
						entitymanager.getTransaction().begin();
						Employee employee = entitymanager.find(Employee.class,Integer.valueOf(employeeId));
						if(employee.getRole().equals("Admin")) {
							return Response.status(200).entity("Cannot delete Admin from the system").build();
						}
						else {
							entitymanager.remove(employee);
							entitymanager.getTransaction().commit();
						}
						entitymanager.close();
						emfactory.close();
						return Response.status(200).entity("success").build();
					}
					catch (Exception e) {
						return Response.status(200).entity("Unable to Delete Employee").build();
					}
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	@POST
	@Path("/GetEmployees")
	@Produces(MediaType.TEXT_PLAIN)
	public Response GetEmployees(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			if(!loginUserRole.equals("Admin")) {
				return Response.status(200).entity("This user doesnt have access for this operation").build();
			}
			else {
				try {
					EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
					EntityManager entitymanager = emfactory.createEntityManager();
					entitymanager.getTransaction().begin();
					Query query = entitymanager.createQuery("SELECT e"+" FROM Employee e");
					List<Employee> list = (List<Employee>)query.getResultList();
					int i =0;
					JSONObject output = new JSONObject();
					for (Employee employee : list) {
						JSONObject obj = new JSONObject();
						obj.put("employeeId", employee.getEmployeeId());
						obj.put("firstname", employee.getFirstname());
						obj.put("lastname", employee.getLastname());
						obj.put("username", employee.getUsername());
						obj.put("password", employee.getPassword());
						obj.put("role", employee.getRole());
						output.put(String.valueOf(i),obj);
						i++;
					}
					entitymanager.close();
					emfactory.close();
					return Response.status(200).entity(output.toString()).build();
				}
				catch (Exception e) {
					return Response.status(200).entity("Unable to fetch Employee Details").build();
				}
			}
		}
	}
	@POST
	@Path("/UpdateEmployeePassword")
	@Produces(MediaType.TEXT_PLAIN)
	public Response UpdateEmployeePassword(@FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword, @FormParam("employeeId") String employeeId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			String loginEmployeeId = String.valueOf(session.getAttribute("employeeId"));
			if( !(loginUserRole.equals("Admin") || loginEmployeeId.equals(employeeId)) ) {
				return Response.status(200).entity("This user doesnt have access to perform this operation").build();
			}
			else {
				if(oldPassword == null || newPassword == null || "".equals(oldPassword) || "".equals(newPassword)) {
					return Response.status(200).entity("Please Enter all details to process the request").build();
				} else {
					try {
						EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
						EntityManager entitymanager = emfactory.createEntityManager();
						entitymanager.getTransaction().begin();
						Employee employee = entitymanager.find(Employee.class,Integer.valueOf(employeeId));
						if(employee.getPassword().equals(oldPassword)) {
							employee.setPassword(newPassword);
							entitymanager.getTransaction().commit();
							entitymanager.close();
							emfactory.close();
							return Response.status(200).entity("success").build();
						}
						else {
							return Response.status(200).entity("Invalid Old Password").build();
						}
					} 
					catch (Exception e) {
						return Response.status(200).entity("Unable to Update Your Password").build();
					}
				}
			}
		}
	}
	@POST
	@Path("/ResetEmployeePassword")
	@Produces(MediaType.TEXT_PLAIN)
	public Response ResetEmployeePassword(@FormParam("password") String password, @FormParam("employeeId") String employeeId, @Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(200).entity("Login is required to access this Page").build();
		} else {
			String loginUserRole = (String) session.getAttribute("role");
			if(!loginUserRole.equals("Admin")) {
				return Response.status(200).entity("This user doesnt have access for this operation").build();
			}
			else {
				if(employeeId == null || "".equals(employeeId) || password == null || "".equals(password)) {
					return Response.status(200).entity("Please Enter All Details Required to Reset Employee Password").build();
				}else {
					try {
						EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Test");
						EntityManager entitymanager = emfactory.createEntityManager();
						entitymanager.getTransaction().begin();
						Employee employee = entitymanager.find(Employee.class,Integer.valueOf(employeeId));
						employee.setPassword(password);
						entitymanager.getTransaction().commit();
						entitymanager.close();
						emfactory.close();
						return Response.status(200).entity("success").build();
					} catch (Exception e) {
						return Response.status(200).entity("Unable to reset Employee Password").build();
					}
				}
			}
		}
	}
}
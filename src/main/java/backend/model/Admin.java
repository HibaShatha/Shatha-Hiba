package backend.model;
/**
 * Represents an Admin in the system.
 * <p>
 * This class stores the admin's username and password,
 * typically used for login or authorization purposes.
 * </p>
 *
 * <p>It's designed as a simple data model without additional logic.</p>
 *
 * @author Shatha_Dweikat
 * @version 1.0
 */
public class Admin {
 public String username;
 public String password;
 
 public Admin(String username,String password) {
	 this.username=username;
	 this.password=password;
 }
  public String getUsername() {return username;}
  public String getPassword() {return password;}
  
}

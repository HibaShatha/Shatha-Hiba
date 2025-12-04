package backend.model;
/**
 * Represents an Admin in the system.
 * <p>
 * Stores the admin's username and password, typically used for login or authorization purposes.
 * </p>
 *
 * <p>This is a simple data model without additional logic.</p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0
 */
public class Admin {

 public String username;
 public String password;

 /**
  * Constructs an Admin with the specified username and password.
  *
  * @param username the username of the admin
  * @param password the password of the admin
  */
 public Admin(String username,String password) {
	 this.username=username;
	 this.password=password;
 }
 /**
  * 
  * Gets the admin's username.
  *
  * @return the username
  */
  public String getUsername() {return username;}
  
  /**
   * Gets the admin's password .
   *
   * @return the password
   */
  public String getPassword() {return password;}
  
}

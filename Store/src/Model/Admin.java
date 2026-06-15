package Model;
import java.util.*;

//Admin class inheriting from User

public class Admin extends User {
	 private List<User> employeeList = new ArrayList<>();
 public Admin( String username, String password, String name, String email,
              String phoneNumber, String accessLevel) {
     super(username, password,name, email, phoneNumber);
     this.name=name;
     this.accessLevel=accessLevel;
     setRole(Role.ADMIN);
 }

     public void addEmployee(User user) {
         employeeList.add(user);
     }

     public void deleteEmployee(User user) {
         employeeList.remove(user);
     }

     public void modifyEmployee(User user) {
         for (int i = 0; i < employeeList.size(); i++) {
             if (employeeList.get(i).getUsername().equalsIgnoreCase(user.getUsername())) {
                 employeeList.set(i, user);
                 return;
             }
         }
         employeeList.add(user);
     }



@Override
 public String toString() {
     return super.toString() + "\n" + "Name: " + name + "\n"
            
             + "Phone Number: " + phoneNumber +"\n"
             +"Email: " + email + "\n"
             +"Access Level: " + accessLevel + "\n";
 }
}

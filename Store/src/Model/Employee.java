package Model;

public class Employee extends User {
    public Employee(String username, String password, String name, String email,
                    String phoneNumber, Role role, String accessLevel) {
        super(username, password, name, email, phoneNumber);
        setRole(role);
        setAccessLevel(accessLevel);
    }

    @Override
    public String toString() {
        return super.toString() + "\nName: " + getName() + "\nEmail: " + getEmail() + "\nPhone: " + getPhoneNumber() + "\nAccess Level: " + getAccessLevel();
    }
}

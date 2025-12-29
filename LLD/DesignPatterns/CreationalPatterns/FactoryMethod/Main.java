public class Main {
    enum UserType {
        ADMIN,
        SUPERUSER
    }   
    static class User {
    }
    static class AdminUser extends User{

    }
    static class Superuser extends User{
    }
   
    static class SimpleUserFactory {
        public static User createUser(UserType type){
            switch (type) {
                case ADMIN:
                    return new AdminUser();
                    case SUPERUSER:
                    return new Superuser();
                default:
                    throw new IllegalArgumentException("Invalid user type");
            }
        }
    }
                
    static abstract class AbstractUser{
        // Enforing behavior as well as workflow
        // Should not just do create part
        // creation should be delegated to subclasses
        // Creation should be part of worflow 
        // As abstract classes use is to share logic/workflow between subclasses, hold states and common behavior
        public User getUser() {
            User user = createUser();   // factory method
            validate(user);
            audit(user);
            return user;
        }
        // Method is Protected otherwise it can be directly created from outside PACKAGE
        protected abstract User createUser();
        
        // Shared and uniform workflow WHCIH IS NOT exposed but shared (Reflects True Abstraction)
        private void validate(User user) {
            System.out.println("Validating " + "");
        }
    
        private void audit(User user) {
            System.out.println("Auditing " + "");
        }

    }
    static class AdminUserCreator extends AbstractUser{
        @Override
        // Method is Protected otherwise it can be directly created from outside PACKAGE
        protected User createUser(){
            return new AdminUser();
        }
    }
    public static void main(String[] args) {
       
     
        User superuser = SimpleUserFactory.createUser(UserType.SUPERUSER);
        

        User user=new AdminUserCreator().createUser();
        System.out.println(user);
        System.out.println(superuser.getClass().getName());

    }
}

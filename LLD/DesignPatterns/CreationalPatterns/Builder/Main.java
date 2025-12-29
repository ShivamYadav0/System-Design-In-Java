public class Main {

    static class User {
        private final String name;
        private final Integer age;
        private final String address;
        private final String gender;

        private User(UserBuilder builder){
            this.name=builder.name;
            this.age=builder.age;
            this.address=builder.address;
            this.gender=builder.gender;
        }

        static class UserBuilder {
            private final String name;
            private final Integer age;
            private String address;
            private String gender;

            UserBuilder(String name, Integer age) {
                this.name=name;
                this.age=age;
            }
            UserBuilder buidWithAddress(String address) {
                this.address=address;
                return this;
            }
            UserBuilder buildWithGender(String gender) {
                this.gender=gender;
                return this;
            }
            User build(){
                if (name == null || age == null) {
                    throw new IllegalStateException("Name and age cannot be null");
                }
                return new User(this);
            }

        }
    }

    public static void main(String[] args) {
        User user = new User.UserBuilder("John", 25).build();
                System.out.println(user.gender);
    }
}

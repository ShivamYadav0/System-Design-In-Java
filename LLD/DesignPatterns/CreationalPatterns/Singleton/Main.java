
public class Main {
    public static class LazySingleton {
        private static LazySingleton INSTANCE;

        private LazySingleton() {
        }

        public static LazySingleton getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new LazySingleton();
            }
            return INSTANCE;
        }
    }
    public static class Singleton {
        static class SingletonHolder {
            private static final Singleton INSTANCE = new Singleton();
        }

        private Singleton() {
        }
        public static Singleton getInstance() {
            return SingletonHolder.INSTANCE;
        }
    }

    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println(s1 == s2);
    }
}

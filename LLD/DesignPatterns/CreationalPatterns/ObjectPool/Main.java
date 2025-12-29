import java.util.concurrent.BlockingQueue;

public class Main {
    static class ObjectPool<T>{
        private final BlockingQueue<T> pool;
        private final Integer SIZE;
        // Supl
        public ObjectPool(Integer size,Supplier<T> objectFactory){

            pool=new LinkedBlockingQueue<>();
            SIZE=size;
            for (int i = 0; i < size; i++) {
                pool.add(objectFactory.get());
            }
        }
        public T acquire() throws InterruptedException{
            return pool.take();
        }
        public void release(T object){
            if (object != null) {
                object.reset();
                pool.offer(object); // Add back to the pool
            }
        }
        public Integer getSize(){
            return SIZE;
        }

    }
    //supplier
    public static void main(String[] args) {
        ObjectPool<ExpensiveResource> resourcePool = new ObjectPool<>(2, ExpensiveResource::new);
        System.out.println(resourcePool.getSize());
    }
}

package Lock.java;

/**
 * 可重入锁：意味着线程可以对它已经拥有的锁再加锁
 */
public class MyReentrantLock {

    private static boolean isLocked = false;
    private static int lockedCounter = 0;
    private static Thread lockedBy = null;

    public synchronized void lock() throws InterruptedException {
        Thread currThread = Thread.currentThread();
        while (isLocked && currThread != lockedBy) {
            wait();
        }
        isLocked = true;
        lockedBy = currThread;
        lockedCounter++;
    }

    public synchronized void unlock() {
        if (lockedBy == Thread.currentThread()) {
            lockedCounter--;
            if (lockedCounter == 0) {
                isLocked = false;
                notifyAll();
            }
        }
    }

}


/**
 * 不可重入锁
 */
class MyLock{
    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException{
        while(isLocked){
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock(){
        isLocked = false;
        notify();
    }
}

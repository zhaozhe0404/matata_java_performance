package thread.java;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java Lock/Condition方法实现生产者消费者模式
 * Condition通信比Object通信的好处在哪？减少无用的唤醒
 * Condition在指定的condition上对线程唤醒，比如消费者唤醒生产者，生产者唤醒消费者，一句lock.notifyAll()即可
 * 而object的notifyAll唤醒了所有线程。
 * 那你为啥不适用object.notify方法呢？
 * 因为可能造成阻塞，比如生产者一直唤醒生产者，消费者饿死
 * @author zhaozhe
 */
public class ProducerConsumerCondition {

    public static Lock lock = new ReentrantLock();
    public static Condition fullCondition = lock.newCondition();
    public static Condition emptyCondition = lock.newCondition();

    public static LinkedList<Integer> queue = new LinkedList<>();

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();
        pc.new Producer().start();
        pc.new Consumer().start();
    }

    /**
     * 生产者
     * 一直生产，直到队列满了，然后再队列上等待
     */
    static class Producer extends Thread {

        @Override
        public void run() {
            int counter = 0;
            while (true) {
                lock.lock();
                while (queue.size() == 10) {
                    try {
                        System.out.println("Queue is full. Producer is waiting.");
                        fullCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.addFirst(counter++);
                fullCondition.notifyAll(); // 生产者同时是等待者和通知者
                emptyCondition.notifyAll();
                System.out.println("Producer produced a number: " + counter);
                try {
                    Thread.sleep(new Random().nextInt(3)*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        }

    }

    /**
     * 消费者
     * 一直消费，知道队列空了，然后在队列上唤醒其他线程
     */
    static class Consumer extends Thread {

        @Override
        public void run() {
            while (true) {
                lock.lock();
                while (queue.isEmpty()) {
                    System.out.println("Consumer is waiting.");
                    try {
                        emptyCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int num = queue.removeLast();
                fullCondition.notifyAll(); // 消费者同时是等待者和通知者
                emptyCondition.notifyAll();
                System.out.println("Consumer consumed a number: " + num);
                try {
                    Thread.sleep(new Random().nextInt(3)*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        }

    }

}

package thread.java;

import java.util.LinkedList;
import java.util.Random;

/**
 * Java wait/notify方法实现生产者消费者模式
 * @author zhaozhe
 */
public class ProducerConsumer {

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
    class Producer extends Thread {

        @Override
        public void run() {
            int counter = 0;
            while (true) {
                synchronized (queue) { // 注意：wait和notify方法要提前对queue加锁
                    while (queue.size() == 10) {
                        try {
                            System.out.println("Queue is full. Producer is waiting.");
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    queue.addFirst(counter++);
                    queue.notifyAll(); // 生产者同时是等待者和通知者
                    System.out.println("Producer produced a number: " + counter);
                    try {
                        Thread.sleep(new Random().nextInt(3)*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * 消费者
     * 一直消费，知道队列空了，然后在队列上唤醒其他线程
     */
    class Consumer extends Thread {

        @Override
        public void run() {
            while (true) {
                synchronized (queue) { // 注意：wait和notify方法要提前对queue加锁
                    while (queue.isEmpty()) {
                        System.out.println("Consumer is waiting.");
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    int num = queue.removeLast();
                    queue.notifyAll();// 消费者同时是等待者和通知者
                    System.out.println("Consumer consumed a number: " + num);
                    try {
                        Thread.sleep(new Random().nextInt(3)*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}

package sourcecodemodeler.controller;

import sourcecodemodeler.ResourceLock;

public class Thread1 extends Thread{

    ResourceLock lock;

    Thread1(ResourceLock lock){
        this.lock = lock;
    }

    @Override
    public void run() {

        try{
            synchronized (lock) {

                for (int i = 0; i < 100; i++) {


                    Thread.sleep(1000);
                    lock.flag = 2;
                    lock.notifyAll();
                }

            }
        }catch (Exception e) {
            System.out.println("Exception 1 :"+e.getMessage());
        }

    }

}


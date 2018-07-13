/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.prs;

import java.util.Random;

/**
 *
 * @author MB
 */
public class Resource {

    private String type;    // P, SD, UD
    private int avgExp, // average processing time
            flow,        // for calculation of resource flow
            avgProcNum;  // average process number
    private double workTime, // for calculationg utilization
            time;               //
    private Queue queue;    // queue for processes
    private Random randomGenerator; // for generation of exp function
    private SimulatedSystem system;

    public Resource(SimulatedSystem system, String type, int avgExp) {
        this.avgExp = avgExp;
        this.system = system;
        this.type = type;

        queue = new Queue(this);
        randomGenerator = new Random();

        time = workTime = 0;
        avgProcNum = 0;
    }

    public double getTime() {
        return time;
    }

    public Queue getQueue() {
        return queue;
    }

    public String getType() {
        return type;
    }
    
    public void setQueue(Queue queue) {
        this.queue = queue;
    }
    
    public int getAvgExp() {
        return avgExp;
    }

    public double getFlow() {
        return flow/system.getTime() * 1000;
    }

    public double getUtilization() {
        return workTime / system.getTime();
    }

    public double getAvgJobNum() {
        return queue.getAvgJobNum() / system.getTime() + getUtilization();
    }

    public int getTotalProcNum(){
        return queue.getTotalProcNum();
    }

    public void addProcessToQueue() {
        queue.addProcess();
    }

    public double getSystemTime() {
        return system.getTime();
    }

    public double getRandomFrom0To1() {
        return randomGenerator.nextFloat();
    }

    public double getExpRandomNumber() {
        return -avgExp * Math.log(getRandomFrom0To1());
    }

    public boolean isFree(){
        return time <= system.getTime();
    }

    public void calculateNextResource() {
        int rand;

        if (type.equalsIgnoreCase("P1") || type.equalsIgnoreCase("P2")) {
            // if processor
            rand = randomGenerator.nextInt(20);
            if(rand < 2){
                // system disk 1
                system.getSystemDisk1().addProcessToQueue();
            } else if(rand < 5){
                // system disk 2
                system.getSystemDisk2().addProcessToQueue();
            } else {
                // user disk
                rand = randomGenerator.nextInt(system.getK());
                system.getUserDisk()[rand].addProcessToQueue();
            }

        } else if(type.equalsIgnoreCase("SD1") || type.equalsIgnoreCase("SD2")){
            // if system disk
            rand = randomGenerator.nextInt(5);
            if (rand < 1) {
                // processors
                rand = randomGenerator.nextInt(10);
                if (rand < 5) {
                    system.getProc1().addProcessToQueue();
                } else {
                    system.getProc2().addProcessToQueue();
                }
            } else {
                // user disk
                rand = randomGenerator.nextInt(system.getK());
                system.getUserDisk()[rand].addProcessToQueue();
            }

        } else {
            // if user disk
            rand = randomGenerator.nextInt(20);
            if(rand < 19){
               // processors
                rand = randomGenerator.nextInt(10);
                if (rand < 5) {
                    system.getProc1().addProcessToQueue();
                } else {
                    system.getProc2().addProcessToQueue();
                }
            } else {
                 // system disk 1
                system.getSystemDisk1().addProcessToQueue();
            }
        }

        // if there is another process in queue
        // than work
        if (queue.getProcess()){
            calculate();
        }
        
    }

    public void calculate(){
        if(isFree()) {
            double sysTime = system.getTime();
            double temp = 0;

            do {
                temp = getExpRandomNumber();
            } while (temp > 1000000);

            flow++;

            avgProcNum += queue.getProcessNum() * temp;
            time = sysTime + temp;
            workTime += temp;

            system.addToResouceList(this);
        }
    }
}

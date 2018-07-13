/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.prs;

/**
 *
 * @author MB
 */
public class Queue {

    private int processNum,     // num of process in queue
            totalProcNum;       // for system response
    private double time,
            avgJobNum;           // average job number
    private Resource res;        // resource that belongs to

    public Queue(Resource res) {
        this.res = res;

        processNum = totalProcNum = 0;
        time = 0;
    }

    public double getAvgJobNum() {
        return avgJobNum;
    }

    public double getProcessNum() {
        return processNum;
    }

    public int getTotalProcNum() {
        return totalProcNum;
    }

    public boolean empty() {
        return processNum == 0;
    }

    public void addProcess() {
        totalProcNum++;
        
        if (empty() && res.isFree()) {
            res.calculate();
        } else if(res.isFree()){
            res.calculate();
        } else {
            double temp = res.getSystemTime() - time;
            avgJobNum += temp * processNum;
            time = res.getSystemTime();

            processNum++;
        }
    }

    public boolean getProcess() {
        if (!empty()) {
            double temp = res.getSystemTime() - time;
            avgJobNum += temp * processNum;
            time = res.getSystemTime();

            processNum--;
            return true;
        }
        return false;
    }
}

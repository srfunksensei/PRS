/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rs.etf.prs;

import java.util.LinkedList;

/**
 *
 * @author MB
 */
public class SimulatedSystem {

    private static final double Xp = 1, Xs1 = 0.9965278, Xs2 = 1.2, Xku = 13.1944444;
    private static final double simTime = 12 * 60 * 60 * 1000;

    private int N, // degree of multiprogramming
            K;     // num of user disks

    private Resource proc1, proc2,
            systemDisk1, systemDisk2,
            userDisk[];
    private LinkedList<Resource> resources;

    private double systemTime;  // current time of simulation

    public SimulatedSystem(int N, int K) {
        this.N = N;
        this.K = K;

        proc1 = new Resource(this, "P1", 3);
        proc2 = new Resource(this, "P2", 3);
        systemDisk1 = new Resource(this, "SD1", 10);
        systemDisk2 = new Resource(this, "SD2", 12);

        userDisk = new Resource[K];
        for (int i = 0; i < userDisk.length; i++) {
            userDisk[i] = new Resource(this, "UD" + (i+1), 20);
        }

        resources = new LinkedList<Resource>();
    }

    public Resource getProc1() {
        return proc1;
    }

    public Resource getProc2() {
        return proc2;
    }

    public Resource getSystemDisk1() {
        return systemDisk1;
    }

    public Resource getSystemDisk2() {
        return systemDisk2;
    }

    public Resource[] getUserDisk() {
        return userDisk;
    }

    public int getK() {
        return K;
    }

    public void addToResouceList(Resource res){
        Resource tempRes = null;
        int i;
        for (i = 0; i < resources.size(); i++) {
            tempRes = resources.get(i);

            if (tempRes.getTime() > res.getTime()) {
                resources.add(i, res);
                break;
            }
        }

        if (i == resources.size()) {
            resources.addLast(res);
        }
    }
    
    /*
     * this section here and all
     * methods are for
     * ANALYTIC method
     */
    private double avgJobNum(double [][] buzenMatrix, double x){
        double temp = 0;
        double tempX = x;

        for (int i = 1; i < N; i++) {
            temp += tempX * buzenMatrix[N - i][K + 4] / buzenMatrix[N][K + 4];
            tempX *= x;
        }

        return temp;
    }

    private double buzenCalculate(double[][] G){
        // initialisation of x
        double x[] = new double[K + 4];
        x[0] = x[1] = Xp;
        x[2] = Xs1;
        x[3] = Xs2;
        for (int i = 4; i < x.length; i++) {
            x[i] = Xku/K;
        }

        // initialisation of G
        for (int i = 0; i < K+4+1; i++) {
            G[0][i] = 1;
        }

        for (int i = 0; i < N+1; i++) {
            G[i][0] = 0;
        }

        //calucating
        for (int i = 1; i < N+1; i++) {
            for (int j = 1; j < K+1+4; j++) {
                G[i][j] = G[i][j-1] + x[j-1] * G[i-1][j];
            }
        }

        return G[N-1][K+4]/G[N][K+4];
    }

    public String analyze(){
        StringBuilder sb = new StringBuilder();

        double buzenMatrix[][]=new double[N+1][K+4+1];
        double G = buzenCalculate(buzenMatrix);

        sb.append("::: P1  :::");
        sb.append("\t utilization: ").append(G*Xp);
        sb.append("\t flow: ").append(G*Xp*1000/proc1.getAvgExp()).append("proc/sec");
        sb.append("\t average job num: ").append(avgJobNum(buzenMatrix, Xp)).append("\n");

        sb.append("::: P2  :::");
        sb.append("\t utilization: ").append(G*Xp);
        sb.append("\t flow: ").append(G*Xp*1000/proc2.getAvgExp()).append("proc/sec");
        sb.append("\t average job num: ").append(avgJobNum(buzenMatrix, Xp)).append("\n");

        sb.append("::: SD1 :::");
        sb.append("\t utilization: ").append(G*Xs1);
        sb.append("\t flow: ").append(G*Xs1*1000/systemDisk1.getAvgExp()).append("proc/sec");
        sb.append("\t average job num: ").append(avgJobNum(buzenMatrix, Xs1)).append("\n");

        sb.append("::: SD2 :::");
        sb.append("\t utilization: ").append(G*Xs2);
        sb.append("\t flow: ").append(G*Xs2*1000/systemDisk2.getAvgExp()).append("proc/sec");
        sb.append("\t average job num: ").append(avgJobNum(buzenMatrix, Xs2)).append("\n");

        for (int i = 0; i < K; i++) {
            sb.append("::: UD").append(i + 1).append(" :::");
            sb.append("\t utilization: ").append(G * Xku/K);
            sb.append("\t flow: ").append((G * Xku/K*1000) / userDisk[i].getAvgExp()).append("proc/sec");
            sb.append("\t average job num: ").append(avgJobNum(buzenMatrix, Xku/K)).append("\n");
        }

        // response of system
        sb.append("system responce: ").append(N/(2*G*Xp/proc1.getAvgExp())).append("\n");

        return sb.toString();
    }
    /*
     * END OF ANALYTIC SECTION
     */


    /*
     * this section here and all
     * methods are for
     * SIMULATION method
     */
    public void initSimulation(){
        // every processor has its' own queue
        for (int i = 0; i < N/2; i++) {
            proc1.addProcessToQueue();
            proc2.addProcessToQueue();
        }
        if(N%2 == 1){
            proc1.addProcessToQueue();
        }
    }

    public String simulate(){

        initSimulation();

        while (systemTime < simTime) {
            Resource res = resources.removeFirst();
            systemTime = res.getTime();
            res.calculateNextResource();
        }
        
        StringBuilder sb = new StringBuilder();

        sb.append("::: P1  :::");
        sb.append("\t utilization: ").append(proc1.getUtilization());
        sb.append("\t flow: ").append(proc1.getFlow()).append("proc/sec");
        sb.append("\t average job num: ").append(proc1.getAvgJobNum()).append("\n");

        sb.append("::: P2  :::");
        sb.append("\t utilization: ").append(proc2.getUtilization());
        sb.append("\t flow: ").append(proc2.getFlow()).append("proc/sec");
        sb.append("\t average job num: ").append(proc2.getAvgJobNum()).append("\n");

        sb.append("::: SD1 :::");
        sb.append("\t utilization: ").append(systemDisk1.getUtilization());
        sb.append("\t flow: ").append(systemDisk1.getFlow()).append("proc/sec");
        sb.append("\t average job num: ").append(systemDisk1.getAvgJobNum()).append("\n");

        sb.append("::: SD2 :::");
        sb.append("\t utilization: ").append(systemDisk2.getUtilization());
        sb.append("\t flow: ").append(systemDisk2.getFlow()).append("proc/sec");
        sb.append("\t average job num: ").append(systemDisk2.getAvgJobNum()).append("\n");

        for (int i = 0; i < K; i++) {
            sb.append("::: UD").append(i + 1).append(" :::");
            sb.append("\t utilization: ").append(userDisk[i].getUtilization());
            sb.append("\t flow: ").append(userDisk[i].getFlow()).append("proc/sec");
            sb.append("\t average job num: ").append(userDisk[i].getAvgJobNum()).append("\n");
        }

        // response of system
        sb.append("system responce: ").append(systemTime*N/(proc1.getTotalProcNum() + proc2.getTotalProcNum())).append("\n");

        return sb.toString();
    }
    /*
     * END OF SIMULATION SECTION
     */

    public String deviation(){
        StringBuilder sb = new StringBuilder();

        double[][] matrix = new double[N+1][K+4+1];
        double G = buzenCalculate(matrix);

        sb.append("::: P1  :::");
        sb.append(" utilization: ").append(100 * Math.abs(proc1.getUtilization() - Xp * G) / (Xp * G));
        sb.append("%\t flow: ").append(100 * Math.abs(proc1.getFlow() - (Xp * G * 1000 / proc1.getAvgExp())) / (Xp * G * 1000 / proc1.getAvgExp()));
        sb.append("%\t avg job num: ").append(100 * Math.abs(proc1.getAvgJobNum() - avgJobNum(matrix, Xp)) / (avgJobNum(matrix, Xp))).append("%\n");

        sb.append("::: P2  :::");
        sb.append(" utilization: ").append(100 * Math.abs(proc2.getUtilization() - Xp * G) / (Xp * G));
        sb.append("%\t flow: ").append(100 * Math.abs(proc2.getFlow() - (Xp * G * 1000 / proc2.getAvgExp())) / (Xp * G * 1000 / proc2.getAvgExp()));
        sb.append("%\t avg job num: ").append(100 * Math.abs(proc2.getAvgJobNum() - avgJobNum(matrix, Xp)) / (avgJobNum(matrix, Xp))).append("%\n");

        sb.append("::: SD1 :::");
        sb.append(" utilization: ").append(100 * Math.abs(systemDisk1.getUtilization() - Xs1 * G) / (Xs1 * G));
        sb.append("%\t flow: ").append(100 * Math.abs(systemDisk1.getFlow() - (Xs1 * G * 1000 / systemDisk1.getAvgExp())) / (Xs1 * G * 1000 / systemDisk1.getAvgExp()));
        sb.append("%\t avg job num: ").append(100 * Math.abs(systemDisk1.getAvgJobNum() - avgJobNum(matrix, Xs1)) / (avgJobNum(matrix, Xs1))).append("%\n");

        sb.append("::: SD2 :::");
        sb.append(" utilization: ").append(100 * Math.abs(systemDisk2.getUtilization() - Xs2 * G) / (Xs2 * G));
        sb.append("%\t flow: ").append(100 * Math.abs(systemDisk2.getFlow() - (Xs2 * G * 1000 / systemDisk2.getAvgExp())) / (Xs2 * G * 1000 / systemDisk2.getAvgExp()));
        sb.append("%\t avg job num: ").append(100 * Math.abs(systemDisk2.getAvgJobNum() - avgJobNum(matrix, Xs2)) / (avgJobNum(matrix, Xs2))).append("%\n");

        for (int i = 0; i < K; i++) {
            sb.append("::: UD").append(i+1).append(" :::");
            sb.append(" utilization: ").append(100 * Math.abs(userDisk[i].getUtilization() - (Xku/K) * G) / ((Xku/K) * G));
            sb.append("%\t flow: ").append(100 * Math.abs(userDisk[i].getFlow() - ((Xku/K) * G * 1000 / userDisk[i].getAvgExp())) / ((Xku/K) * G * 1000 / userDisk[i].getAvgExp()));
            sb.append("%\t avg job num: ").append(100 * Math.abs(userDisk[i].getAvgJobNum() - avgJobNum(matrix, (Xku/K))) / (avgJobNum(matrix, (Xku/K)))).append("%\n");
        }
        
        return sb.toString();
    }
    
    public double getTime() {
        return systemTime;
    }
}

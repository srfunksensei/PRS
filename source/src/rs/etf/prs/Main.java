/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rs.etf.prs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author MB
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String sim, ana, dev;

        // create directory for results
        checkDir(new File("results").mkdir());
        
        for (int i = 10; i < 30; i += 5) {
            ana = "results/ana_N=" + i + ".txt";
            sim = "results/sim_N=" + i + ".txt";
            dev = "results/dev_N=" + i + ".txt";

            try {
                PrintWriter analytic = new PrintWriter(ana),
                        simulation = new PrintWriter(sim),
                        deviation = new PrintWriter(dev);

                for (int j = 2; j < 7; j++) {
                    SimulatedSystem sys = new SimulatedSystem(i, j);

                    analytic.println("K="+ j + "\n" + sys.analyze());
                    simulation.println("K="+ j + "\n" + sys.simulate());
                    deviation.println("K="+ j + "\n" + sys.deviation());
                }

                analytic.close();
                simulation.close();
                deviation.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Error while opening file!");
            }
        }
    }

    private static void checkDir(boolean event) {
        if (event == true) {
            System.out.println("Created directory");
        }
        if (event == false) {
            System.out.println("Error: mkdirs failed to create a directory");
        }
    }
}


package cia.ui;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import cia.processor.Processor;

public class CommandLineUserInterface {

    protected Processor processor;
    protected Scanner in;
    
    public CommandLineUserInterface(Processor processor) {
        this.processor = processor;
        in = new Scanner(System.in);
        
    }
    
    public void start() {
        System.out.println("Welcome. Here is your menu");
        System.out.println("");
        System.out.println(" [0] List countries in (continent) that are prone to (natural hazard)");
        System.out.println(" [1] List countries with (element) in their flag");
        System.out.println(" [2] Find country with lowest elevation point in (continent)");
        System.out.println(" [3] List countries in (continent) that have at least (percentage) of their land covered in (land use)");
        System.out.println(" [4] Find top (number) countries with highest electricity consumption per capita");
        System.out.println(" [5] List countries where dominant ethnic group accounts for (percentage) of population"); 
        System.out.println(" [6] List landlocked countries");
        System.out.println(" [7] List countries that export more crude oil than they import");
        System.out.println(" [8] EXIT");
        System.out.println("");
        System.out.println("Enter your selection: ");
        
        int choice = in.nextInt();
        
        if (choice == 0) {
            withHazard();
        } else if (choice == 1) {
            flagsWith();
        } else if (choice == 2) {
            lowest();
        } else if (choice == 3) {
            coverage();
        } else if (choice == 4) {
            highestConsumers();
        } else if (choice == 5) {
            dominantMajority();
        } else if (choice == 6) {
            landlocked();
        } else if (choice == 7) {
            exporters();
        } else if (choice == 8) {
            System.out.println("Thank you for using the service");
            in.close();
        } else {
            System.out.println("Invalid choice");
            in.close();
        }
        
    }

    private void exporters() {
        // call processor
        List<String> l = processor.getExp();
        for (String country : l) {
            System.out.println(" - " + country);
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
        
    }

    private void landlocked() {
        // call processor
        List<String> l = processor.getLocked();
        for (String country : l) {
            System.out.println(" - " + country);
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }

    private void dominantMajority() {
        System.out.println("Enter percentage: ");
        Double p = in.nextDouble();
        while (p < 1 || p > 100) {
            System.out.println("Invalid");
            System.out.println("Enter percentage: ");
            p = in.nextDouble(); 
        }
        // call processor
        List<String> ans = processor.getMajority(p);
        for (String country : ans) {
            System.out.println(" - " + country);
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }

    private void highestConsumers() {
        System.out.println("Enter number: ");
        int p = in.nextInt();
        while (p < 1 || p > 240) {
            System.out.println("Invalid");
            System.out.println("Enter number: ");
            p = in.nextInt(); 
        }
        // call processor
        Map<String, Double> ans = processor.getHighestConsumers(p);
        int count = 1;
        for (Entry elt : ans.entrySet()) {
            if (count > p) {
                break;
            }
            System.out.println(" - " + elt.getKey() + " consumes " + elt.getValue() + "KwH");
            count++;
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }

    private void coverage() {
        System.out.println("Enter continent: ");
        String cont = in.next();
        System.out.println("Enter percentage: ");
        double p = in.nextDouble();
        while (p < 1 || p > 100) {
            System.out.println("Invalid");
            System.out.println("Enter percentage: ");
            p = in.nextInt(); 
        }
        System.out.println("Enter type of land: ");
        String land = in.next();
        List<String> ans = processor.getCoverage(cont, p, land);
        System.out.println("Countries in " + cont + " with " + p + "% of their land covered in " + land + " are:");
        for (String country : ans) {
            System.out.println(" - " + country);
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }

    private void lowest() {
        System.out.println("Enter continent: ");
        String cont = in.next();
        String ans = processor.getLowestIn(cont);
        System.out.println(ans);
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }

    private void flagsWith() {
        System.out.println("Enter flag element: ");
        String elt = in.next();
        
        List<String> ans = processor.getFlagsWith(elt);
        // print
        System.out.println("Countries with a " + elt + " in their flag are:");
        for (String country : ans) {
            System.out.println(" - " + country);
        }
        
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }

    private void withHazard() {
        System.out.println("Enter continent: ");
        String cont = in.next();
        System.out.println("Enter hazard: ");
        String hazard = in.next();
        List<String> ans = processor.getCountriesWithHazard(cont, hazard);
        System.out.println("Countries at risk of " + hazard + " are: ");
        for (String country : ans) {
            System.out.println(" - " + country);
        }
        System.out.println("");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("");
        in.close();
        start();
    }
}

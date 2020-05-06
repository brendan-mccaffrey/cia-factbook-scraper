package cia.main;

import cia.parser.Parser;
import cia.processor.Processor;
import cia.ui.CommandLineUserInterface;

public class Main {

    public static void main(String[] args) {
        
        Parser parse = new Parser("https://www.cia.gov/library/publications/the-world-factbook/");
        Processor processor = new Processor(parse);
        
        CommandLineUserInterface ui = new CommandLineUserInterface(processor);
        ui.start();
        
        

    }
    
    

}

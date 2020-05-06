package cia.main;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import cia.parser.Parser;

public class MainTest {
    
    Parser parse;
    
    @Before
    public void setUp() {
        parse = new Parser("https://www.cia.gov/library/publications/the-world-factbook/");
        parse.getCountries();
        System.out.println("set-up done");
    }

    @Test
    public void testIsHazard() {
        assertTrue(parse.isHazard("Afghanistan", "earthquake"));
    }

    @Test
    public void testPrintContinentCountries() {
        parse.printContinentCountries();
    }
    
    @Test
    public void testFindLowestElevation() {
        parse.getLowestPoint("Cambodia");
        assertTrue(true);
    }
    
    @Test 
    public void testIsCovered() {
        assertTrue(parse.isCovered("Cambodia", 40, "forest"));
        
    }
    
    @Test
    public void testGetPopulation() {
        assertEquals(16926984, parse.getPopulation("Cambodia"));
    }
    
    @Test
    public void testGetPopulationWords() {
        assertEquals(new BigDecimal(3997000), parse.getPopulation("Georgia"));
    }
    
    @Test
    public void testGetElectricity() {
        assertEquals(16926984, parse.getElectricity("Cambodia"));
    }
    
    @Test
    public void testGetMajority() {
        assertTrue(parse.majority("Albania", 80.0));
    }
    
    @Test
    public void testLocked() {
        assertTrue(parse.isLocked("Austria"));
    }
    
    @Test
    public void testExp() {
        assertTrue(parse.isExp("Senegal"));
    }
}

package cia.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cia.parser.Parser;

public class Processor {

    protected Parser p;
    protected List<String> countryList;
    
    public Processor(Parser parse) {
        p = parse;
        countryList = p.getCountries();
    }
    
    // HAZARD
    
    public List<String> getCountriesWithHazard(String continent, String hazard) {
        List<String> countries = p.getCountriesIn(continent);
        if (countries == null) {
            return null;
        } else {
            List<String> ans = new ArrayList<>();
            for (String country : countries) {
                if (p.isHazard(country, hazard)) {
                    ans.add(country);
                }
            }
            return ans;
        }
    }
    
    // FLAG
    
    public List<String> getFlagsWith(String element) {
        List<String> ans = new ArrayList<>();
        for (String country: countryList) {
            if (p.flagContains(country, element)) {
                ans.add(country);
            }
        }
        return ans;
    }
    
    // ELEVATION
    
    public String getLowestIn(String continent) {
        int tempLow = 999999;
        String currWinner = "";
        List<String> countries = p.getCountriesIn(continent);
        if (countries == null) {
            return "Invalid continent";
        } else {
            for (String country : countries) {
                if (p.getLowestPoint(country) < tempLow) {
                    tempLow = p.getLowestPoint(country);
                    currWinner = country;
                }
            }
            String ans = currWinner + " has the lowest elevation at " + tempLow;
            return ans;
        }
    }
    
    // LAND COVERAGE
    
    public List<String> getCoverage(String cont, double percent, String land) {
        List<String> countries = p.getCountriesIn(cont);
        if (countries == null) {
            return null;
        } else {
            List<String> ans = new ArrayList<>();
            for (String country : countries) {
                if (p.isCovered(country, percent, land)) {
                    ans.add(country);
                }
            }
            return ans;
        }
    }
    
    public Map<String, Double> getHighestConsumers(int num) {
        Map<String, Double> unsortedMap = new TreeMap<String, Double>();
        for (String country : countryList) {
            unsortedMap.put(country, p.getConsumption(country));
        }
        LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
        unsortedMap.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
        .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }
    
    public List<String> getMajority(Double per) {
        List<String> l = new LinkedList<String>();
        for (String country : countryList) {
            if (p.majority(country, per)) {
                l.add(country);
            }
        }
        return l;
    }
    
    public List<String> getLocked() {
        List<String> l = new LinkedList<String>();
        for (String country : countryList) {
            if (p.isLocked(country)) {
                l.add(country);
            }
        }
        return l;
    }
    
    public List<String> getExp() {
        List<String> l = new LinkedList<String>();
        for (String country : countryList) {
            if (p.isExp(country)) {
                l.add(country);
            }
        }
        return l;
    }


}

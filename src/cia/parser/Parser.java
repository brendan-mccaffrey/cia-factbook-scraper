package cia.parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
    
    private String baseurl;
    private Document tempDoc;
    private Map<String, String> countryMap;
 
    public ArrayList<String> continents = new ArrayList<>(Arrays. 
            asList("Asia", "North America", "South America", "Africa", "Europe", "Oceania", "Antartica", "Unknown"));
    public ArrayList<String> noLocationInfoCountries = new ArrayList<>();
    public Map<String, List<String>> continentCountries;
    
    // list country info section IDs here
    String geoSectionID = "geography-category-section";
    
    String hazardID = "field-natural-hazards";
    String locationID = "field-location";
    String continentID = "field-map-references";
    String elevationID = "field-elevation";
    String landID = "field-land-use";
    String coastID = "field-coastline";
    
    String govSectionID = "government-category-section";
    
    String flagID = "field-flag-description";
    
    String peopleSectionID = "people-and-society-category-section";
    
    String popID = "field-population";
    String groupsID = "field-ethnic-groups";
    
    String energySectionID = "energy-category-section";
    String energyID = "field-electricity-consumption";
    String oilExID = "field-crude-oil-exports";
    String oilImID = "field-crude-oil-imports";
    
   
    public Parser(String url) {
        this.baseurl = url;
        try {
            this.tempDoc = Jsoup.connect(baseurl).get();
        } catch (IOException e) {
            System.out.println("Couldn't connect to page");
        }
        setCountries();
        setContinentCountries();
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
// Instantiate variables
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
  
    public void setCountries() {
        this.countryMap = new HashMap<String, String>();
        Elements countryElts = this.tempDoc.select("option");
        for (Element country : countryElts) {
            String countryURL = country.attr("value");
            String name = country.text();
            if (name.contains("world") || name.contains("Please")) {
                
            } else {
                this.countryMap.put(name, countryURL);
            }
        } 
    }
    
    public void setContinentCountries() {
        // instantiate map
        this.continentCountries = new HashMap<String, List<String>>();
        for (String elt : continents) {
            this.continentCountries.put(elt, new LinkedList<String>());
        }
        
        for (Entry<String, String> elt : countryMap.entrySet()) {
            String name = elt.getKey();
            if (continents.contains(getContinent(name))) {
                this.continentCountries.get(getContinent(name)).add(name);
            } else {
                noLocationInfoCountries.add(getContinent(name));
            }
        }
    }
    
    
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
// Get page of country
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
  
    
    public String getCountryPage(String countryName) {
        // error handling
        if (!countryMap.containsKey(countryName)) {
            System.out.println("We don't have data on that country.");
            return "";
        }
        
        String url = baseurl + countryMap.get(countryName);
        
        try {
            this.tempDoc = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println("Couldn't connect to " + countryName + "'s page");
        }
        return url;
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
// Retrieve section of info on country
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
    
    public Elements getCountryGeo(String countryName) {
        getCountryPage(countryName);
        Elements topics = tempDoc.select("li");
        for (Element topic : topics) {
            if (topic.attr("id").equals(geoSectionID)) {
                Elements infoList = topic.select("div");
                return infoList;
            }
        }
        return null;
    }
    
    public Elements getCountryGov(String countryName) {
        getCountryPage(countryName);
        Elements topics = tempDoc.select("li");
        for (Element topic : topics) {
            if (topic.attr("id").equals(govSectionID)) {
                Elements infoList = topic.select("div");
                return infoList;
            }
        }
        return null;
    }
    
    public Elements getCountryPeople(String countryName) {
        getCountryPage(countryName);
        Elements topics = tempDoc.select("li");
        for (Element topic : topics) {
            if (topic.attr("id").equals(peopleSectionID)) {
                Elements infoList = topic.select("div");
                return infoList;
            }
        }
        return null;
    }
    
    public Elements getCountryEnergy(String countryName) {
        getCountryPage(countryName);
        Elements topics = tempDoc.select("li");
        for (Element topic : topics) {
            if (topic.attr("id").equals(energySectionID)) {
                Elements infoList = topic.select("div");
                return infoList;
            }
        }
        return null;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
// Retrieve info within section
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
    
    // Geography Section
    
    public boolean isHazard(String countryName, String hazard) {
        
        Elements infoList = getCountryGeo(countryName);
        for (Element info : infoList) {
            if (info.attr("id").equals(hazardID)) {
                return info.text().contains(hazard);
            }
        }
        throw new IllegalArgumentException("No natural hazard section found");
    }
    
    public boolean isCovered(String countryName, double percent, String land) {
        Elements infoList = getCountryGeo(countryName);
        for (Element info : infoList) {
            if (info.attr("id").equals(landID)) {
                String useInfo = info.text();
                Pattern p = Pattern.compile("[\\w | \\s]+:[\\s][\\d | \\.]+");
                Matcher m = p.matcher(useInfo);
                while (m.find()) {
                    if (m.group().contains(land)) {
                        String[] pieces = m.group().split(" ");
                        double amount = Double.parseDouble(pieces[pieces.length - 1]);
                        if (Double.compare(amount, percent) > 0) {
                            return true;
                        } else {
                            return false;
                        }
                    };
                }
            }
        }
        System.out.println("No natural hazard section found for " + countryName);
        return false;
    }
    
    public int getLowestPoint(String countryName) {
        Elements infoList = getCountryGeo(countryName);
        for (Element info : infoList) {
            if (info.attr("id").equals(elevationID)) {
                Elements elevations = info.select("div");
                for (Element elt : elevations) {
                    if (elt.attr("class").equals("category_data subfield text")) {
                        if (elt.text().contains("lowest")) {
                            Pattern p = Pattern.compile("[\\d | -]+");
                            Matcher m = p.matcher(elt.text());
                            if (m.find()) {
                                return Integer.valueOf(m.group());
                            }
                        }
                    }
                }
            }
        }
        return 99999;
    }
    
    
    
    // Government Section
    
    public boolean flagContains(String countryName, String element) {
        
        Elements infoList = getCountryGov(countryName);
        for (Element info : infoList) {
            if (info.attr("id").equals(flagID)) {
                return info.text().contains(element);
            }
        }
        return false;
    }
    
    // Energy Section
    
    public Double getConsumption(String country) {
        if (noLocationInfoCountries.contains(country)) {
            return 0.0;
        }
        BigDecimal electricity = getElectricity(country);
        BigDecimal population = getPopulation(country);
        if (population.equals(new BigDecimal(0))) {
            return 0.0;
        } else {
            
            BigDecimal quotient = electricity
                    .divide(population, 3, RoundingMode.HALF_EVEN);
            Double ans = Double.valueOf(quotient.toString());
            return ans;
        }
        
    }
    
    public boolean majority(String country, Double p) {
        Elements infoList = getCountryPeople(country);
        if (infoList == null) {
            return false;
        }
        for (Element info : infoList) {
            if (info.attr("id").equals(groupsID)) {
                Pattern patt = Pattern.compile("\\d[\\d | \\.]+%");
                Matcher m = patt.matcher(info.text());
                if (m.find()) {
                    String num = m.group().substring(0, m.group().length() - 1);
                    double percent = Double.valueOf(num);
                    return (p.compareTo(percent) < 0);
                }
                return false;
            }
        }
        return false;
    }
    
    public boolean isLocked(String country) {
        Elements infoList = getCountryGeo(country);
        if (infoList == null) {
            return false;
        }
        for (Element info : infoList) {
            if (info.attr("id").equals(coastID)) {
                Pattern patt = Pattern.compile("\\d");
                Matcher m = patt.matcher(info.text());
                if (m.find()) {
                    return (m.group().equals("0"));
                }
                return false;
            }
        }
        return false;
    }
    
    public boolean isExp(String country) {
        Elements infoList = getCountryEnergy(country);
        if (infoList == null) {
            return false;
        }
        BigDecimal imp = oilImp(country, infoList);
        BigDecimal exp = oilExp(country, infoList);
        return exp.compareTo(imp) > 0;
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
// Helper Methods
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
  
    public BigDecimal oilExp(String countryName, Elements infoList) {
        String amt = "";
        if (infoList == null) {
            return new BigDecimal(0);
        }
        for (Element info : infoList) {
            String mult = null;
            if (info.attr("id").equals(oilExID)) {
                Pattern p = Pattern.compile("\\d[\\d | \\. | , | \\s | \\w]+?bbl/day");
                Matcher m = p.matcher(info.text());
                if (m.find()) {
                    String[] pieces = m.group().split(" ");
                    String num = pieces[0];
                    String[] parts = pieces[0].split(",");
                    for (int i = 0; i < parts.length; i++) {
                        amt += parts[i];
                    }
                    if (amt.trim().isEmpty()) {
                        return new BigDecimal(0);
                    }
                    if (pieces.length > 1) {
                        if (pieces[1].equals("thousand")) {
                            mult = "1000";
                        } else if (pieces[1].equals("million")) {
                            mult = "1000000";
                        } else if (pieces[1].equals("billion")) {
                            mult = "1000000000";
                        } 
                        if (mult == null) {
                            return new BigDecimal(amt);
                        }
                        return (new BigDecimal(mult)).multiply(new BigDecimal(amt));
                    } else {
                        //System.out.println("~~~~~~~~~~~~~~~~"+countryName + " and "+pop);
                        return new BigDecimal(amt);
                    }
                }
            }
        }
        return new BigDecimal(0);
    }
    
    public BigDecimal oilImp(String countryName, Elements infoList) {
        String amt = "";
        if (infoList == null) {
            return new BigDecimal(0);
        }
        for (Element info : infoList) {
            String mult = null;
            if (info.attr("id").equals(oilImID)) {
                Pattern p = Pattern.compile("\\d[\\d | \\. | , | \\s | \\w]+bbl/day");
                Matcher m = p.matcher(info.text());
                if (m.find()) {
                    String[] pieces = m.group().split(" ");
                    String num = pieces[0];
                    String[] parts = pieces[0].split(",");
                    for (int i = 0; i < parts.length; i++) {
                        amt += parts[i];
                    }
                    if (amt.trim().isEmpty()) {
                        return new BigDecimal(0);
                    }
                    if (pieces.length > 1) {
                        if (pieces[1].equals("thousand")) {
                            mult = "1000";
                        } else if (pieces[1].equals("million")) {
                            mult = "1000000";
                        } else if (pieces[1].equals("billion")) {
                            mult = "1000000000";
                        } 
                        if (mult == null) {
                            return new BigDecimal(amt);
                        }
                        return (new BigDecimal(mult)).multiply(new BigDecimal(amt));
                    } else {
                        //System.out.println("~~~~~~~~~~~~~~~~"+countryName + " and "+pop);
                        return new BigDecimal(amt.toString());
                    }
                }
            }
        }
        return new BigDecimal(0);
    }
    
    public List<String> getCountriesIn(String continent){
        if (!this.continentCountries.containsKey(continent)) {
            return null;
        }
        return this.continentCountries.get(continent);
    }
    
    public List<String> getCountries() {
        ArrayList<String> countries = new ArrayList<String>();
        countries.addAll(this.countryMap.keySet());
        return countries;
    }
    
    public BigDecimal getPopulation(String countryName) {
        String pop = "";
        Elements infoList = getCountryPeople(countryName);
        if (infoList == null) {
            return new BigDecimal(0);
        }
        for (Element info : infoList) {
            String mult = null;
            if (info.attr("id").equals(popID)) {
                Pattern p = Pattern.compile("\\d[\\d | \\. | , | \\s | \\w]+");
                Matcher m = p.matcher(info.text());
                if (m.find()) {
                    String[] pieces = m.group().split(" ");
                    String num = pieces[0];
                    String[] parts = pieces[0].split(",");
                    for (int i = 0; i < parts.length; i++) {
                        pop += parts[i];
                    }
                    if (pop.trim().isEmpty()) {
                        return new BigDecimal(0);
                    }
                    if (pieces.length > 1) {
                        if (pieces[1].equals("thousand")) {
                            mult = "1000";
                        } else if (pieces[1].equals("million")) {
                            mult = "1000000";
                        } else if (pieces[1].equals("billion")) {
                            mult = "1000000000";
                        } 
                        if (mult == null || num == null) {
                            return new BigDecimal(pop);
                        }
                        return (new BigDecimal(mult)).multiply(new BigDecimal(pop));
                    } else {
                        //System.out.println("~~~~~~~~~~~~~~~~"+countryName + " and "+pop);
                        return new BigDecimal(pop.toString());
                    }
                }
            }
        }
        return new BigDecimal(0);
    }
    
    public BigDecimal getElectricity(String countryName) {
        Elements infoList = getCountryEnergy(countryName);
        String mult = null;
        if (infoList == null) {
            return new BigDecimal("0");
        }
        for (Element info : infoList) {
            if (info.attr("id").equals(energyID)) {
                Pattern p = Pattern.compile("[\\d | \\. | \\s | \\w]+kWh");
                Matcher m = p.matcher(info.text());
                if (m.find()) {
                    String[] pieces = m.group().split(" ");
                    String num = pieces[0];
                    
                    if (pieces[1].equals("thousand")) {
                        mult = "1000";
                    } else if (pieces[1].equals("million")) {
                        mult = "1000000";
                    } else if (pieces[1].equals("billion")) {
                        mult = "1000000000";
                    } else if (pieces[1].equals("trillion")) {
                        mult = "1000000000000";
                    }
                    if (num == null) {
                        return new BigDecimal("0");
                    } else if (mult == null) {
                        return new BigDecimal(num);
                    }
                    return (new BigDecimal(mult)).multiply(new BigDecimal(num));
                }
            }
        }
        return new BigDecimal("0");
    }
    
    // Helper for setContinentCountries
    
    public String getContinent(String countryName) {
        
        Elements infoList = getCountryGeo(countryName);
        if (infoList == null) {
            return countryName;
        }
        for (Element info : infoList) {
            if (info.attr("id").equals(locationID)) {
                for (String elt : continents) {
                    if (info.text().contains(elt)) {
                        return elt;
                    } 
                }
                if (info.text().contains("Middle East")) {
                    return "Asia";
                } else if (info.text().contains("Caribbean")) {
                    return "North America";
                } else if (info.text().contains("Indian Ocean")) {
                    return "Africa";
                } else if (countryName.equals("Dhekelia") || countryName.equals("Akrotiri")
                        || countryName.contains("Turks") || countryName.contains("Clipperton")) {
                    return "Europe";
                } else if (countryName.equals("Antarctica")) {
                    return countryName;
                } else if (countryName.contains("Bahamas") || countryName.contains("Salvador")) {
                    return "North America";
                } else {
                    return "Unknown";
                }
                   
            }
        }
        return countryName;
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
// Print Methods
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
    

    public void printCountryMap() {
        for (Entry<String, String> entry : countryMap.entrySet()) {
            System.out.println(entry);
        }
    }
    
    public void printContinentCountries() {
        for (Entry<String, List<String>> entry : continentCountries.entrySet()) {
            System.out.println(entry.getKey());
            for (String country : entry.getValue()) {
                System.out.println(" - " + country);
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (String elt : noLocationInfoCountries) {
            System.out.println(elt);
        }
    }

}

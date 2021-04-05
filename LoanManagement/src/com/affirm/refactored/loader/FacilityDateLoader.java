package com.affirm.refactored.loader;

import com.affirm.refactored.entity.Bank;
import com.affirm.refactored.entity.Facility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FacilityDateLoader {

    private  String bankFile;
    private  String facilityFile;
    private  String covenantsFile;


    public FacilityDateLoader(String bankFile, String facilityFile, String covenantsFile){
        this.bankFile = bankFile;
        this.covenantsFile = covenantsFile;
        this.facilityFile = facilityFile;
    }

    /**
     * Load the facility and bank data from csv. I'm not reading Banks.csv intentionally as I dont see the need for it.
     * @return
     */
    public  List<Facility>  load(){
        //temporary map to populate entity object. Once the load object execution is over, these objects will become inactive
        Map<String, Bank> bankHolder = new HashMap<>();
        Map<String, Facility> facilityHolder = new HashMap<>();
        List<Facility> facilities = loadFacilities(bankHolder,facilityHolder);
        //Sort the facilities object by Cheapest Interest Rates , if interest rates are same, then use facility amount.
        Collections.sort(facilities, (a,b) -> {
            int interestComparator = Float.compare(a.getInterestRate(),b.getInterestRate());
            if(interestComparator == 0){
                return Double.compare(a.getAmount(),b.getAmount());
            }
            return interestComparator;
        });
        //DEBUG STATEMENT: System.out.println("********** after sort **********************");
       // DEBUG STATEMENT: facilities.stream().forEach(System.out::println);
        updateCovenants(bankHolder,facilityHolder);
        //DEBUG STATEMENT: System.out.println("********** after applying covenants **********************");
        //DEBUG STATEMENT: facilities.stream().forEach(System.out::println);
        return facilities;
    }

    private  List<Facility> loadFacilities(Map<String, Bank> bankHolder, Map<String, Facility> facilityHolder){
        List<Facility> facilities = new LinkedList<>();
        //DEBUG STATEMENT : System.out.println("********** "+facilityFile+" **********************");
        try (Scanner fileScanner = new Scanner(new File(facilityFile))) {
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split(",",-1);
                Facility facility = new Facility(Integer.parseInt(data[2]));
                facility.setAmount(Double.parseDouble(data[0]));
                facility.setInterestRate(Float.parseFloat(data[1]));
                int bankId = Integer.parseInt(data[3]);
                facility.setBank(bankHolder.getOrDefault(data[2],new Bank(bankId)));
                facilityHolder.put(data[2],facility);
                facilities.add(facility);
                bankHolder.put(data[2],facility.getBank());
            }
        } catch (FileNotFoundException e) {
            System.out.println(facilityFile+ " not_found. Exception = "+e);
        }
        return facilities;
    }

    private  void updateCovenants(Map<String, Bank> bankHolder, Map<String, Facility> facilityHolder){
        try (Scanner fileScanner = new Scanner(new File(covenantsFile))) {
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                Optional<Float> maxDefaultLimit = Optional.empty();
                String state = null;

                String[] data = fileScanner.nextLine().split(",",-1);
                if (data[1] != null && !data[1].isEmpty()) {
                    maxDefaultLimit = Optional.of(Float.valueOf(data[1]));
                }
                if (data[3] != null && !data[3].isEmpty()) {
                    state = data[3];
                }
                //FacilityId can be null even though the provided  data sheet doesnt have it. The requirment doc says it.
                //testdata sheet under resources will have data with empty facilityId
                if (data[0] != null && !data[0].isEmpty()) {
                    Facility facility = facilityHolder.get(data[0]);
                    if(maxDefaultLimit.isPresent())
                        facility.setMaxDefaultLikelihood(maxDefaultLimit.get());
                    facility.addBannedStates(state);
                }else{
                    Bank bank = bankHolder.get(data[2]);
                    if(maxDefaultLimit.isPresent())
                        bank.setMaxDefaultLikelihood(maxDefaultLimit.get());
                    bank.addBannedStates(state);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(covenantsFile + " not_found. Exception = " + e);
        }
    }
}

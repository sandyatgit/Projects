package com.affirm.legacy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

//NOTE : THIS IS MY FIRST SIMPLE IMPLEMENTATION TO QUICKLY GET A SOLUTION. THE SOLUTION HAS BEEN REFACTORED INTO OBJECT ORIENTED MODEL AND PACKAGED
//INSIDE "refactored"
public class LoanAssignment {

    Map<Integer,Set<String>> bankStateCovenant = new HashMap();
    Map<Integer,Set<String>> facilityStateCovenant = new HashMap();
    Map<Integer,Float> bankDefaultLimitCovenant = new HashMap();
    Map<Integer,Float> facilityDefaultLimitCovenant = new HashMap();
    Map<Integer,Integer> assignedLoans = new HashMap<>();
    Map<Integer,Double> loanYieldsByFacility = new HashMap<>();
    private static String inBasepath = "resource/large/";
    private static String outBasepath = "resource/out/large/";

    public static void main(String[] args) {
        LoanAssignment obj = new LoanAssignment();

        List<String[]> unAssignedLoans = obj.readCSVFile(inBasepath+"loans.csv");
        unAssignedLoans.stream().forEach(x -> System.out.println(Arrays.toString(x)));

        List<String[]> facilities = obj.readCSVFile(inBasepath+"facilities.csv");
        facilities.stream().forEach(x -> System.out.println(Arrays.toString(x)));
        Collections.sort(facilities, (a,b) -> {
            int interestComparator = Double.compare(Double.parseDouble(a[1]),Double.parseDouble(b[1]));
            if(interestComparator == 0){
               return Double.compare(Double.parseDouble(a[0]),Double.parseDouble(b[0]));
            }
            return interestComparator;
        });
        System.out.println("********** after sort **********************");

        facilities.stream().forEach(x -> System.out.println(Arrays.toString(x)));

        List<String[]> convenants = obj.readCSVFile(inBasepath+"covenants.csv");
        convenants.stream().forEach(x -> System.out.println(Arrays.toString(x)));

        obj.readCovenantsFile(inBasepath+"covenants.csv");

        obj.processLoans(facilities,unAssignedLoans);

    }

    private void processLoans(List<String[]> facilities, List<String[]> loans){
        for(String[] loan : loans){
            double loanAmount = Double.parseDouble(loan[1]);
            String loaneeState = loan[4];
            int loanId = Integer.parseInt(loan[2]);
            float loanInterestRate = Float.parseFloat(loan[0]);
            float defaultLikeliwood = Float.parseFloat(loan[3]);
            ListIterator<String[]> facilityItr = facilities.listIterator();
            double loanYield = -1.0f;
            while(facilityItr.hasNext()) {
                String[] facility = facilityItr.next();
                double facilityAmount = Double.parseDouble(facility[0]);
                if (loanAmount < facilityAmount) {
                    int facilityId = Integer.parseInt(facility[2]);
                    int bankId = Integer.parseInt(facility[3]);
                    float facilityInterestRate = Float.parseFloat(facility[1]);
                    if (bankStateCovenant.containsKey(bankId) && bankStateCovenant.get(bankId).contains(loaneeState)) {
                        continue;
                    }
                    if (facilityStateCovenant.containsKey(facilityId) && facilityStateCovenant.get(facilityId).contains(loaneeState)) {
                        continue;
                    }
                    if (bankDefaultLimitCovenant.containsKey(bankId) && defaultLikeliwood > bankDefaultLimitCovenant.get(bankId)) {
                        continue;
                    }
                    if (facilityDefaultLimitCovenant.containsKey(facilityId) && defaultLikeliwood > facilityDefaultLimitCovenant.get(facilityId)) {
                        continue;
                    }
                    facilityAmount = facilityAmount - loanAmount;
                    facility[0] = String.valueOf(facilityAmount);
                    assignedLoans.put(loanId,facilityId);
                    loanYield = (1-defaultLikeliwood)*loanInterestRate*loanAmount - defaultLikeliwood*loanAmount - facilityInterestRate*loanAmount;
                    System.out.println("loan Id = "+loanId+" facilityId = "+facilityId+" loanYield = "+loanYield);
                    double currield = loanYieldsByFacility.getOrDefault(facilityId,0.0d);
                    loanYieldsByFacility.put(facilityId,currield+loanYield);
                    if(facilityAmount == 0.0d)
                        facilityItr.remove();
                    break;
                }
            }
        }
        assignedLoans.keySet().stream().forEach(x -> System.out.println("loan Id = "+x+" facility Id = "+assignedLoans.get(x)));
        loanYieldsByFacility.keySet().stream().forEach(x -> System.out.println("facilityd Id = "+x+" yield = "+loanYieldsByFacility.get(x)));
        writeToCSVFile(outBasepath+"yields-legacy.csv",loanYieldsByFacility, new String[]{"facility_id","expected_yield"});
        writeToCSVFile(outBasepath+"assignments-legacy.csv",assignedLoans,new String[]{"loan_id","facility_id"});
    }

    public <T, Y> void writeToCSVFile(String fileName, Map<T,Y> dataLines, String[] headers) {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(headers[0]+","+headers[1]);
            dataLines.keySet().stream().map(x->{
                return x+","+dataLines.get(x);
            }).forEach(pw::println);
        } catch (FileNotFoundException e) {
            System.out.println(fileName+ " not_found. Exception = "+e);
        }
    }



    private List<String[]> readCSVFile(String fileName){
        List<String[]> data = new LinkedList();
        System.out.println("********** "+fileName+" **********************");
        try (Scanner fileScanner = new Scanner(new File(fileName));) {
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                data.add(fileScanner.nextLine().split(","));
            }
        } catch (FileNotFoundException e) {
            System.out.println(fileName+ " not_found. Exception = "+e);
        }
        return data;
     }



    private void readCovenantsFile(String filename){
        System.out.println("********** "+filename+" **********************");
        try (Scanner fileScanner = new Scanner(new File(filename));) {
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                Float maxDefaultLimit = null;
                Integer facilityId = null;
                int bankId = -1;
                String[] data = fileScanner.nextLine().split(",",-1);
                for(int i=0; i<data.length;i++){
                    switch(i) {
                        case 0:
                            String fId = data[i];
                            if (fId != null && !fId.isEmpty()) {
                                facilityId = Integer.valueOf(fId);
                            }
                            break;
                        case 1:
                            String maxDefault = data[i];
                            if (maxDefault != null && !maxDefault.isEmpty()) {
                                maxDefaultLimit = Float.valueOf(maxDefault);
                            }
                            break;
                        case 2:
                            bankId = Integer.parseInt(data[i]);
                            if(maxDefaultLimit != null) {
                                if (facilityId == null)
                                    bankDefaultLimitCovenant.put(bankId, maxDefaultLimit);
                                else
                                    facilityDefaultLimitCovenant.put(facilityId,maxDefaultLimit);
                            }
                            break;
                        case 3:
                            String state = data[i];
                            if (state != null && !state.isEmpty()) {
                                if (facilityId == null) {
                                    Set<String> states = bankStateCovenant.getOrDefault(bankId, new HashSet<String>());
                                    states.add(state);
                                    bankStateCovenant.put(bankId,states);
                                }else {
                                    Set<String> states = facilityStateCovenant.getOrDefault(facilityId, new HashSet<String>());
                                    states.add(state);
                                    facilityStateCovenant.put(facilityId,states);
                                }
                            }
                    }
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println(filename + " not_found. Exception = " + e);
        }

        for(Integer key : bankStateCovenant.keySet()){
            System.out.print("\n"+key+" -> ");
            bankStateCovenant.get(key).stream().forEach(x -> System.out.print(x+","));
        }
        bankDefaultLimitCovenant.keySet().stream().forEach(x-> System.out.println(x+" - "+bankDefaultLimitCovenant.get(x)));
        for(Integer key : facilityStateCovenant.keySet()){
            System.out.print("\n"+key+" -> ");
            facilityStateCovenant.get(key).stream().forEach(x -> System.out.print(x+","));
        }
        facilityDefaultLimitCovenant.keySet().stream().forEach(x-> System.out.println(x+" - "+facilityDefaultLimitCovenant.get(x)));

    }



}

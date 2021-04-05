package com.affirm.refactored;

import com.affirm.refactored.loader.FacilityDateLoader;
import com.affirm.refactored.loader.LoanDataLoader;
import com.affirm.refactored.model.LoanData;
import com.affirm.refactored.service.LoanProcessor;
import com.affirm.refactored.writer.CSVWriter;

//NOTE : This class is under refactored package meaning all classes under refactored packages are refactored into Object oriented model after a simple version
        // has been written which is under legacy package.
/**
 * LoanManager is the main class that manages and orchestrates the loan process with different classes.
 */
public class LoanManager {

    private static String inBasepath = "resource/large/";
    private static String outBasepath = "resource/out/large/";
    private static FacilityDateLoader facilityLoader;
    private static LoanDataLoader loanLoader;
    private static LoanProcessor processor = new LoanProcessor();
    private static CSVWriter csvWriter = new CSVWriter(outBasepath);;

    public static void main(String... s){
        //Context object to carry data across methods
        LoanData contextObj = new LoanData();
        preLoanProcess(contextObj);
        processLoan(contextObj);
        postLoanProcess(contextObj);
    }

    private static void preLoanProcess(LoanData contextObj){
        facilityLoader = new FacilityDateLoader(inBasepath+"banks.csv",inBasepath+"facilities.csv",inBasepath+"covenants.csv");
        contextObj.setFacilities(facilityLoader.load());
    }

    /**
     * Process loans.
     * @param contextObj
     */
    private static void processLoan(LoanData contextObj){
        loanLoader = new LoanDataLoader(inBasepath+"loans.csv");
        contextObj.setLoans(loanLoader.load());
        //Returns a seperate list of fully usedup Facilities. This is done to make the algorithm efficient as the original lookup list will become thinner.
        contextObj.setFullyUsedUpFacilities(processor.processLoans(contextObj.getFacilities(), contextObj.getLoans()));
    }
    private static void postLoanProcess(LoanData contextObj) {
        contextObj.getFullyUsedUpFacilities().addAll(contextObj.getFacilities());
        /**DEBUG STATEMENT START: System.out.println("printing facility object");
        contextObj.getFullyUsedUpFacilities().stream().forEach(System.out::println);
        System.out.println("printing loan object");
        contextObj.getLoans().stream().forEach(System.out::println); DEBUG STATEMENT END*/
        csvWriter.writeToCSVFile(contextObj.getFullyUsedUpFacilities(),contextObj.getLoans());
    }
}

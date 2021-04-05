package com.affirm.refactored.writer;

import com.affirm.refactored.entity.Loan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class LoanAssignmentsWriter {

    public  static void writeToCSVFile(String fileName, String[] headers, List<Loan> loans) {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(headers[0]+","+headers[1]);
            loans.stream().filter(x -> x.getFacilityId()!=null).map(x->{
                return x.getId()+","+x.getFacilityId();
            }).forEach(pw::println);
        } catch (FileNotFoundException e) {
            System.out.println(fileName+ " not_found. Exception = "+e);
        }
    }
}

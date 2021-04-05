package com.affirm.refactored.writer;

import com.affirm.refactored.entity.Facility;
import com.affirm.refactored.entity.Loan;

import java.util.List;

public class CSVWriter {
    private static String outBasepath;

    public CSVWriter(String path) {
        outBasepath = path;
    }

    public void writeToCSVFile(List<Facility> facilities, List<Loan> loans) {
        LoanAssignmentsWriter.writeToCSVFile(outBasepath+"assignments.csv",new String[]{"loan_id","facility_id"},loans);
        YieldsWriter.writeToCSVFile(outBasepath+"yields.csv",new String[]{"facility_id","expected_yield"},facilities);
    }
}

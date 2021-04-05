package com.affirm.refactored.loader;

import com.affirm.refactored.entity.Loan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LoanDataLoader {
    private String loanFile;
    private List<Loan> loans = new LinkedList<>();


    public LoanDataLoader(String file) {
        this.loanFile = file;
    }

    /**
     * Load the loan data from csv.
     * @return
     */
    public List<Loan> load() {
        List<Loan> loans = new LinkedList<>();
        try (Scanner fileScanner = new Scanner(new File(loanFile))) {
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split(",", -1);
                Loan loan = new Loan(Integer.parseInt(data[2]));
                loan.setInterestRate(Float.parseFloat(data[0]));
                loan.setAmount(Double.parseDouble(data[1]));
                loan.setDefaultLikelihood(Float.parseFloat(data[3]));
                loan.setState(data[4]);
                loans.add(loan);
            }
        } catch (FileNotFoundException e) {
            System.out.println(loanFile + " not_found. Exception = " + e);
        }
        return loans;
    }
}

package com.affirm.refactored.model;

import com.affirm.refactored.entity.Facility;
import com.affirm.refactored.entity.Loan;

import java.util.List;

public class LoanData {
    private List<Loan> loans = null;
    private List<Facility> facilities = null;
    private List<Facility> fullyUsedUpFacilities = null;

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Facility> getFullyUsedUpFacilities() {
        return fullyUsedUpFacilities;
    }

    public void setFullyUsedUpFacilities(List<Facility> fullyUsedUpFacilities) {
        this.fullyUsedUpFacilities = fullyUsedUpFacilities;
    }



}

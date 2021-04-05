package com.affirm.refactored.entity;


public class Loan {

    private int id;
    private float interestRate;
    private double amount;
    private Integer facilityId; // This will be used while writing output csv
    private String state;
    private float defaultLikelihood;

    public Loan(int id){
        this.id = id;
    }

    public void setDefaultLikelihood(float defaultLikelihood) {
        this.defaultLikelihood = defaultLikelihood;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public void setState(String state) {
        this.state = state;
    }


    public float getInterestRate() {
        return interestRate;
    }

    public float getDefaultLikelihood() {
        return defaultLikelihood;
    }

    public double getAmount() {
        return amount;
    }

    public String getState() {
        return state;
    }


    public Integer getFacilityId() {
        return facilityId;
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString(){
        return "Loan_id = "+id+" facility_Id = "+facilityId+" interest_rate = "+interestRate+" amount = "+amount+" defaultLikelihood = "+defaultLikelihood
                +" state = "+state;
    }
}

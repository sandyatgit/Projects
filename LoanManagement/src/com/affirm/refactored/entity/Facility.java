package com.affirm.refactored.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Facility {

    private int id;
    private Bank bank;
    private float interestRate;
    private double amount;
    private Double yield; // This will be used while writing output csv
    private Set<String> bannedStates ;
    private Float maxDefaultLikelihood;

    public Facility(int id){
        this.id = id;
        bannedStates = new HashSet();
    }
    public void addBannedStates(String state){
        bannedStates.add(state);
    }
    public void setMaxDefaultLikelihood(float rate){
        this.maxDefaultLikelihood = rate;
    }

    public void setBank(Bank bank){
        this.bank=bank;
    }


    public void setAmount(double amount){
        this.amount = amount;
    }


    public int getId() {
        return id;
    }


    public Double getYield() {
        return yield;
    }

    //deduct the loanamount from facility account and compute yield for this sanctioned loan
    public void sanctionLoan(Loan loan){
        setAmount(amount- loan.getAmount());
        double loanYield = (1-loan.getDefaultLikelihood())*loan.getInterestRate()*loan.getAmount() -
                loan.getDefaultLikelihood()*loan.getAmount() - interestRate*loan.getAmount();
        yield = yield!=null? yield :0.0f;
        yield = yield+loanYield;
    }

    public void setInterestRate(float rate) {
        this.interestRate=rate;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public Bank getBank() {
        return bank;
    }

    /**
     * validate if loan is eligibile with the facility
     * @param loan
     * @return
     */
    public boolean validateLoanEligibility(Loan loan){
        if(loan.getAmount() > amount){
            return false;
        }
        //Ensure overarching bank covenants are good before checking facility ones.
        //Bank covenants are only available when the facility field is empty in covenants.csv file
        if(!bank.validateCovenants(loan.getDefaultLikelihood(),loan.getState())){
            return false;
        }
        if(!validateCovenants(loan.getDefaultLikelihood(),loan.getState())){
            return false;
        }
        return true;
    }

    /**
     * returns true if covenants check passed
     * @return
     */
    public boolean validateCovenants(float defaultLikelihood, String state){
        //Check if its part of banned states
        if(!bannedStates.contains(state)){
            //if this was not set or set but the defaultlikelihood is higher than loandefaultlikelihood return true;
            if(maxDefaultLikelihood == null || maxDefaultLikelihood >= defaultLikelihood){
                return true;
            }
            return false;
        }
        return false;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString(){
        return "Facility_id = "+id+" yield = "+yield+" interest_rate = "+interestRate+" amount = "+amount+" maxDefaultLikelihood = "+maxDefaultLikelihood+" states = "+
                bannedStates.stream().collect(Collectors.joining(","))+" bank = "+bank;
    }

}

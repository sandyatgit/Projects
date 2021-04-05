package com.affirm.refactored.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Bank {
    private int id;
    private String name;
    private Set<String> bannedStates ;
    private Float maxDefaultLikelihood;

    public Bank(int id){
        this.id = id;
        bannedStates = new HashSet();
    }
    public void addBannedStates(String state){
        bannedStates.add(state);
    }
    public void setMaxDefaultLikelihood(float rate){
       this.maxDefaultLikelihood = rate;
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

    @Override
    public String toString(){
        return "Bank_id = "+id+" maxDefaultLikelihood = "+maxDefaultLikelihood+" states = "+
                bannedStates.stream().collect(Collectors.joining(","));
    }

}

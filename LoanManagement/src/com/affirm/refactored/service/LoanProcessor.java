package com.affirm.refactored.service;

import com.affirm.refactored.entity.Facility;
import com.affirm.refactored.entity.Loan;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LoanProcessor {

    /**
     * Process loans. Use the facility list and loan list to process. Once a facility is used up fully, move it to different list so
     * the lookup list contains only with data that will be used for future loans. this should improve performance especially when trying to find potential match
     * for a loan.
     * @param facilities
     * @param loans
     * @return
     */
    public List<Facility> processLoans(List<Facility> facilities, List<Loan> loans){
        List<Facility> fullyUsedUpFacilities = new LinkedList<>();

        for(Loan loan : loans){
            ListIterator<Facility> facilityItr  =  facilities.listIterator();
            while(facilityItr.hasNext()) {
                Facility facility = facilityItr.next();
                //if loan is not eligibile for this facility, then continue the loop and look for other facility.
                if(!facility.validateLoanEligibility(loan))
                    continue;

                facility.sanctionLoan(loan);
                loan.setFacilityId(facility.getId());
                //This ensures that lookup list is cleanedup reqularly after they are being used up so as to improve performance .
                if(facility.getAmount() == 0){
                    fullyUsedUpFacilities.add(facility);
                    facilityItr.remove();
                }
                break;
            }
        }
        return fullyUsedUpFacilities;
    }

}

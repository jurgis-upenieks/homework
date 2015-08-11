package com.homework;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

public class LoanDao {
	private Map<Long, Loan> loans;
	private Long lastUnusedId;

	public void setLoans(Map<Long, Loan> loans) {
		this.loans = loans;
	}
	
	public Loan getLoan(Long loanId) {
		return loans.get(loanId);
	}
	
	public Map<Long, Loan> getLoans() {
		return loans;
	}

	public void setLastUnusedId(Long lastUnusedId) {
		this.lastUnusedId = lastUnusedId;
	}
	
	public void createLoan(Loan loan) {
		loans.put(lastUnusedId++, loan);
	}
	
	public void deleteLoan(Long loanId) throws Exception {
		if (!loans.containsKey(loanId)) {
			throw new Exception("Cannot delete loan by the requested Id, becuse such loan doesnt exist.");
		} else {
			loans.remove(loanId);
		}
	}
	
	public void updateLoan(Long loanId, Loan loan) throws Exception {
		if (!loans.containsKey(loanId)) {
			throw new Exception("Cannot update loan by the requested Id, becuse such loan doesnt exist.");
		} else {
			loans.put(loanId, loan);
		}
	}
}

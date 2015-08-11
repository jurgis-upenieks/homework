package com.homework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.codehaus.jackson.map.ObjectMapper;

@EnableAutoConfiguration
@Controller
@RequestMapping("/loan")
public class LoanController {
	static LoanDao loanDao;
	private static ObjectMapper jsonMapper = new ObjectMapper();
	static HttpHeaders responseHeaders;
	static List<Long> lastMinuteRequestTimestamps = new ArrayList<Long>();
	private static int maxRequestsPerMinute;
	
	static {
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@RequestMapping(value = "/apply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createLoan(@RequestBody String requestBody) {
		Long currentTimestamp = System.currentTimeMillis() / 1000L;
		
		while (lastMinuteRequestTimestamps.size() > 0) {
			if (lastMinuteRequestTimestamps.get(0) < (currentTimestamp - 60)) {
				lastMinuteRequestTimestamps.remove(0);
			} else {
				break;
			}
		}
		if (lastMinuteRequestTimestamps.size() > maxRequestsPerMinute) {
			return new ResponseEntity<String>("{\"error\": \"Error creating loan: Exceeding " + maxRequestsPerMinute + " requests per minute for lv region.\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
		
		try {
			Loan loan = jsonMapper.readValue(requestBody, Loan.class);
			if (!PersonController.personDao.getPersons().containsKey(loan.getPersonId())) {
				return new ResponseEntity<String>("{\"error\": \"Error creating loan: A person with specified id doesn't exist.\"}", responseHeaders, HttpStatus.BAD_REQUEST);
			}
			if (!PersonController.personDao.getPerson(loan.getPersonId()).getBlacklisted()) {
				return new ResponseEntity<String>("{\"error\": \"Error creating loan: A person with specified id is blacklisted.\"}", responseHeaders, HttpStatus.BAD_REQUEST);
			}
			loanDao.createLoan(loan);
			lastMinuteRequestTimestamps.add(currentTimestamp);
			return new ResponseEntity<String>(jsonMapper.writeValueAsString(loan), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error creating loan: " + e.getMessage().replace("\"", "").replace("'", "") + "\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/{loanId}", method = RequestMethod.GET)
	public ResponseEntity<String> getLoan(@PathVariable Long loanId) {   
		Loan loan = loanDao.getLoan(loanId);
		if (loan == null) {
			return new ResponseEntity<String>("{\"error\": \"A loan by the requested Id does not exist.\"}", responseHeaders, HttpStatus.NOT_FOUND);
		}
		try {
			return new ResponseEntity<String>(jsonMapper.writeValueAsString(loan), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error retrieving loan.\"}", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<Long, Loan> getLoans() {
		return loanDao.getLoans();
	}
	
	@RequestMapping(value = "/{loanId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteLoan(@PathVariable Long loanId) {
		try {
			loanDao.deleteLoan(loanId);
			return new ResponseEntity<String>("{\"success\": \"Loan by the requested Id deleted.\"}", responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error deleting loan:" + e.getMessage().replace("\"", "").replace("'", "") + "\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/{loanId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateLoan(@PathVariable Long loanId, @RequestBody String requestBody) {
		try {
			Loan loan = jsonMapper.readValue(requestBody, Loan.class);
			if (!PersonController.personDao.getPersons().containsKey(loan.getPersonId())) {
				return new ResponseEntity<String>("{\"error\": \"Error creating loan: A person with specified id doesn't exist.\"}", responseHeaders, HttpStatus.BAD_REQUEST);
			}
			loanDao.updateLoan(loanId, loan);
			return new ResponseEntity<String>(jsonMapper.writeValueAsString(loan), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error updating loan: " + e.getMessage().replace("\"", "").replace("'", "") + "\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/all-approved", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<Long, Loan> getApprovedLoans() {
		Map<Long, Loan> approvedLoans = new HashMap<Long, Loan>();
		for (Long loanId : loanDao.getLoans().keySet()) {
			if (loanDao.getLoan(loanId).getApproved()) {
				approvedLoans.put(loanId, loanDao.getLoan(loanId));
			}
		}
		return approvedLoans;
	}
	
	@RequestMapping(value = "/all-approved-by-person/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<Long, Loan> getApprovedLoansByUser(@PathVariable Long personId) {
		Map<Long, Loan> approvedLoans = new HashMap<Long, Loan>();
		for (Long loanId : loanDao.getLoans().keySet()) {
			if (loanDao.getLoan(loanId).getApproved() && loanDao.getLoan(loanId).getPersonId() == personId) {
				approvedLoans.put(loanId, loanDao.getLoan(loanId));
			}
		}
		return approvedLoans;
	}

	public static void setLoanDao(LoanDao loanDao) {
		LoanController.loanDao = loanDao;
	}

	public static void setMaxRequestsPerMinute(int maxRequestsPerMinute) {
		LoanController.maxRequestsPerMinute = maxRequestsPerMinute;
	}
}

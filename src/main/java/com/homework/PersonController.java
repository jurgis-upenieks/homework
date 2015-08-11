package com.homework;

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
@RequestMapping("/person")
public class PersonController {
	public static PersonDao personDao;
	private static ObjectMapper jsonMapper = new ObjectMapper();
	static HttpHeaders responseHeaders;
	
	static {
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createPerson(@RequestBody String requestBody) {
		try {
			Person person = jsonMapper.readValue(requestBody, Person.class);
			personDao.createPerson(person);
			return new ResponseEntity<String>(jsonMapper.writeValueAsString(person), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error creating person: " + e.getMessage().replace("\"", "").replace("'", "") + "\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/{personId}", method = RequestMethod.GET)
	public ResponseEntity<String> getPerson(@PathVariable Long personId) {   
		Person person = personDao.getPerson(personId);
		if (person == null) {
			return new ResponseEntity<String>("{\"error\": \"A person by the requested Id does not exist.\"}", responseHeaders, HttpStatus.NOT_FOUND);
		}
		try {
			return new ResponseEntity<String>(jsonMapper.writeValueAsString(person), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error retrieving person.\"}", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<Long, Person> getPersons() {
		return personDao.getPersons();
	}
	
	@RequestMapping(value = "/{personId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> deletePerson(@PathVariable Long personId) {
		try {
			personDao.deletePerson(personId);
			// Deleting related loans
			for (Long loanId : LoanController.loanDao.getLoans().keySet()) {
				if (LoanController.loanDao.getLoan(loanId).getPersonId() == personId) {
					LoanController.loanDao.deleteLoan(loanId);
				}
			}
			return new ResponseEntity<String>("{\"success\": \"Person by the requested Id and it's loans deleted.\"}", responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error deleting person:" + e.getMessage().replace("\"", "").replace("'", "") + "\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/{personId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> updatePerson(@PathVariable Long personId, @RequestBody String requestBody) {
		try {
			Person person = jsonMapper.readValue(requestBody, Person.class);
			personDao.updatePerson(personId, person);
			return new ResponseEntity<String>(jsonMapper.writeValueAsString(person), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"error\": \"Error updating person: " + e.getMessage().replace("\"", "").replace("'", "") + "\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}

	public static void setPersonDao(PersonDao personDao) {
		PersonController.personDao = personDao;
	}
	
}

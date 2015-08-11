package com.homework;

import java.util.Map;

public class PersonDao {
	private Map<Long, Person> persons;
	private Long lastUnusedId;
	
	public void setPersons(Map<Long, Person> persons) {
		this.persons = persons;
	}
	
	public Person getPerson(Long personId) {
		return persons.get(personId);
	}
	
	public Map<Long, Person> getPersons() {
		return persons;
	}

	public void setLastUnusedId(Long lastUnusedId) {
		this.lastUnusedId = lastUnusedId;
	}
	
	public void createPerson(Person person) {
		persons.put(lastUnusedId++, person);
	}
	
	public void deletePerson(Long personId) throws Exception {
		if (!persons.containsKey(personId)) {
			throw new Exception("Cannot delete person by the requested Id, becuse such person doesnt exist.");
		} else {
			persons.remove(personId);
		}
	}
	
	public void updatePerson(Long personId, Person person) throws Exception {
		if (!persons.containsKey(personId)) {
			throw new Exception("Cannot update person by the requested Id, becuse such person doesnt exist.");
		} else {
			persons.put(personId, person);
		}
	}
}

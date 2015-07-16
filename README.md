### Problem definition
Create a tiny RESTful web service with the following business requirements:

- Application must expose REST API endpoints for the following functionality:
  - apply for loan (loan amount, term, name, surname and personal id must be provided)
  - list all approved loans
  - list all approved loans by user
- User should be able to ask question publicly by providing question text.
- Service must perform loan application validation according to the following rules and reject application if:
  - Loan application comes from blacklisted personal id
  - N loan application / second are received from a single country (essentially we want to limit number of loan applications coming from a country in a given timeframe)
- Service must perform origin country resolution using the following web service and store country code together with the loan application. Because networking is unreliable and services tend to fail, let's agree on default country code - "lv".

### Technical requirements

You have total control over framework and tools, as long as application is written in Java. Feel free to write tests in any JVM language.

### What gets evaluated

- Conformance to business requirements
- Code quality, including testability
- How easy it is to run and deploy the service (don't make us install Oracle database please ;)

**Good luck and have fun!**

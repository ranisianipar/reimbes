# Reims
Reims is a reimbursement management system web-based application, which is mobile-friendly. Developed by [Rani Lasma Uli][raniGithub] and Stelli.
## Tools
  - Database: H2
  - Framework: Spring Boot
  - Testing: JUnit5



## Database
Not like postgreSQL, H2 provide an API to implements the super and subclasses in RDBM by
using @Inheritance, for further info check this out: [Complete Guide: Inheritance strategies with JPA and Hibernate][1]

> Why **H2** and not **PostgreSQL**?
H2 support RDBM by OOP concept. Personally, i think H2 have stronger connection with Java than PostgreSQL.<br>
To compare it in more specific topic, visit this link. [Comparison H2 vs PostgreSQL][2]

[raniGithub]: https://github.com/ranisianipar

[1]: https://thoughts-on-java.org/complete-guide-inheritance-strategies-jpa-hibernate/
[2]: https://db-engines.com/en/system/H2%3BPostgreSQL
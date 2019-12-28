# Simple forum
## Description
Simple forum it simple application create by Spring Framework and Derby. This web application enables: adding topics and answers, as well as browsing, deleting some of them.
## Technologies
- Java 11:
  - Spring Framework:
    - Spring MVC:
      - application-level on the basis on design pattern: model-view-controller
    - Spring Data:
      - using JPQL and ready-made methods from `JpaRepository` to creating, reading, updating and deleting data
      - implementation of native queries
    - Spring Security:
      - own login and registration form with authentication of users on the basis of database
    - Spring Boot:
      - automatic configuration and launching application 
  - JPA & Hibernate:
    - specifying relations between entities in database and parameters of columns in tables
  - Java 11 SE:
    - Optionals, LocalDateTime
- HTML:
  - Thymeleaf
  - data validation in login form and registration form
- CSS:
  - BootStrap
- JavaScript
  - JQuery

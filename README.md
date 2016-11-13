## Using MySQL in Spring Boot via Spring Data JPA and Hibernate

### Create database

- Open database dump `\dump.sql` in workbench
- Start database
- Open the `\src\main\resources\application.properties` file and set your own configurations

### Build and run

- Run the application and go on http://localhost:8080/

### Usage
- Fill form. It will be received controller create method
- Use the following urls to invoke controllers methods and see the interactions
  with the database:
    * `/delete?id=[id]`: delete the user with the passed id.
    * `/get-by-email?email=[email]`: retrieve the id for the user with the passed email address
    * `/update?id=[id]&email=[email]&name=[name]`: update the email and the name for the user indentified by the passed id

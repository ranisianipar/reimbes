# Reims
Reims is a reimbursement management system web-based application, which is mobile-friendly. Developed by [Rani Lasma Uli][myGithub] and Stelli.
## Tools
  - Database: PostgreSQL
  - Framework: Spring Boot
  - Testing: JUnit5

## API Spec

### User
| METHOD | URL | NOTES | ROLE
| ------ | ------ | ------ | ------ |
| GET | [/api/users][PlUser] | get all users | (authorized) USER
| GET | [/api/users/{id}][PlUserId] | get users by ID | (authorized) USER
| POST | [/api/users][PlUser] | create a user | ADMIN | ADMIN
| POST | [/api/users/{id}][PlUserId] | update user data | (authorized) USER
| POST | [/api/login][PlLogin] | authentication, create session | ALL
| POST | [/api/logout][PlLogout] | clear session | ALL
| DELETE | [/api/users][PlUser] | delete all users | ADMIN
| DELETE | [/api/users/{id}][PlUserId] | delete a user | ADMIN

#### Filtering
**/api/users?&page=P&size=S&sortBy=email**
- support pagination on URL Path, add this on url: &page=P&size=S&sortBy=date

#### Request Body
**[/api/users][PlUser]**
> {
> "email":"xxxxxxx@yyyyy.com",
> "password":"zzzzzzz"
> }

**[/api/login][PlLogin]**
> {
> "email":"xxxxxxx@yyyyy.com",
> "password":"zzzzzzz"
> }

### Transaction
only for authenticated User

| METHOD | URL | NOTES
| ------ | ------ | ------ |
| GET | [/api/transactions][PlTransaction] | get all transactions |
| GET | [/api/transactions/{id}][PlTransactionId] | get transaction by ID |
| GET | [/api/transactions/_monthly-report][PlTransactionReport] | download transaction |
| POST | [/api/transactions][PlTransaction] | create a transaction |
| DELETE | [/api/transactions][PlTransaction] | delete all transation |
| DELETE | [/api/transactions/{id}][PlTransactionId] | delete a transaction |

#### Filtering
**/api/transactions?month=mm&year=yyyy&category=ALL**
- example value: month=04&&year=2001&&category=PARKING
- category: enum [ALL; FUEL; PARKING]
- support pagination on URL Path, add this on url: &page=P&size=S&sortBy=date
#### Request Body
**/api/transactions/_monthly_report**
> {
>   "month":"mm",
>   "year":"yyyy",
>   "category":"FUEL"
> }
**Note: category could be ALL, FUEL, or PARKING*



### Additional

 - Is there any method not mentioned?

   [mygithub]: <https://github.com/ranisianipar>
   [PlUser]: <https://localhost:8080/api/users>
   [PlUserId]: <https://localhost:8080/api/users/1>
   [PlLogin]: <https://localhost:8080/api/login>
   [PlLogout]: <https://localhost:8080/api/logout>
   
   [PlTransaction]: <https://localhost:8080/transactions>
   [PlTransactionId]: <https://localhost:8080/transactions/1>
   [PlTransactionReport]: <https://localhost:8080/transactions/_monthly_report>
   
   
  

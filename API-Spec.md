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
**/api/users?page=P&size=S&sortBy=email**
- support pagination on URL Path, add this on url: page=P&size=S&sortBy=date

#### Request Body
[POST] **[/api/users][PlUser]**
``` json
{
"email":"xxxxxxx@yyyyy.com",
"password":"zzzzzzz"
}
```
[POST] **[/api/login][PlLogin]**
``` json
{
"email":"xxxxxxx@yyyyy.com",
"password":"zzzzzzz"
}
```
#### Response Body
[POST] **[/api/login][PlLogin]**
**Note: value of the header*
``` json
{
    "Authorization":"Bearer xxxx"
}
```
[GET] **[/api/users][PlUser]**
``` json
{
    "code": 200,
    "errorCode": null,
    "errorMessage": null,
    "totalRecords": 10,
    "totalPages": 2,
    "paging": {
       "size": 7,
       "sortBy":"ASC"
    },
    "value": [
        {
        "id":1,
        "username":"user1"
        }, 
        {
        "id":2,
        "username":"user2"
        }    
    ]
}
```
[GET] **[/api/users/1][PlUserId]**
``` json
{
    "code": 200,
    "errorCode": null,
    "errorMessage": null,
    "totalRecords": 1,
    "totalPages": 1,
    "paging": null,
    "value": [
        {
        "id":1,
        "username":"user1"
        }    
    ]
}
```

### Transaction
only for authenticated User

| METHOD | URL | NOTES
| ------ | ------ | ------ |
| GET | [/api/transactions][PlTransaction] | get all transactions |
| GET | [/api/transactions/{id}][PlTransactionId] | get transaction by ID |
| GET | [/api/transactions/_show-image][PlTransactionReport] | show image in byte |
| GET | [/api/transactions/_ocr-this][PlTransactionReport] | request for OCR Service |
| POST | [/api/transactions][PlTransaction] | create a transaction |
| DELETE | [/api/transactions][PlTransaction] | delete all transation |
| DELETE | [/api/transactions/{id}][PlTransactionId] | delete a transaction |

#### Filtering
**/api/transactions?month=mm&year=yyyy&category=ALL**
- example value: month=04&&year=2001&&category=PARKING
- category: enum [ALL; FUEL; PARKING]
- support pagination on URL Path, add this on url: page=P&size=S&sortBy=date
#### Request Body

[POST] **[/api/transactions][PlTransaction]**
``` json
{
"image": {}
}
```

[GET] **[/api/transactions/_upload][PlTransactionReport]**
``` json
{
"imageValue":"[][]",
}
```
**Note: value of imageValue is Array of Binary (BLOB)

[GET] **[/api/transactions/_show-image][PlTransactionReport]**
``` json
{
"imagePath":"1/1040142LHGWLljagbow.jpg"
}
```

[GET] **[/api/transactions/_ocr-this][PlTransactionReport]**
``` json
{
"imagePath":"1/1040142LHGWLljagbow.jpg"
}
```
#### Response Body
[GET] **[/api/transactions][PlTransaction]**
``` json
{
    "code": 200,
    "errorCode": null,
    "errorMessage": null,
    "totalRecords": 10,
    "totalPages": 2,
    "paging": null,
    "value": [
        {
        "id":1,
        "imagePath":"user1/transaction1.jpg",
        "category":"FUEL",
        "amount":125000,
        "notes": null,
        "purchasedDate":"12/04/2010"
        ....
        }, 
        {
         // transaction object   
        }    
    ]
}
```
[GET] **[/api/transactions/1][PlTransactionId]**
``` json
{
    "code": 200,
    "errorCode": null,
    "errorMessage": null,
    "totalRecords": 10,
    "totalPages": 2,
    "paging": null,
    "value": [
        {
        "id":1,
        "imagePath":"/user1/transaction1.jpg",
        "category":"FUEL",
        "amount":125000,
        "notes": null,
        "purchasedDate":"12/04/2010"
        ....
        }
    ]
}
```

[GET] **[/api/transactions/monthly-report][PlTransactionReport]**
``` json
{
    "code": 200,
    "errorCode": null,
    "errorMessage": null,
    "value": []
}
```

### Additional

 - Is there any method not mentioned?

   [mygithub]: <https://github.com/ranisianipar>
   [PlUser]: <https://localhost:8080/api/users>
   [PlUserId]: <https://localhost:8080/api/users/1>
   [PlLogin]: <https://localhost:8080/api/login>
   [PlLogout]: <https://localhost:8080/api/logout>
   
   [PlTransaction]: <https://localhost:8080/transactions>
   [PlTransactionId]: <https://localhost:8080/transactions/1>
   [PlTransactionReport]: <https://localhost:8080/transactions/monthly-report>
   
   
  

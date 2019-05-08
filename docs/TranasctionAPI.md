# Transaction
only for authenticated User

# List of API
- [Get All transactions](#get-all-transactions)
- [Get transaction by ID](#get-transaction-by-id)
- [Get image](#get-image)
- [OCR](#ocr)
- [Create Transaction](#create-transaction)
- [Upload Image](#upload-image)
- [Delete all transactions](#delete-all-transactions)
- [Delete transaction by ID](#delete-transaction-by-id)

| METHOD | URL | NOTES
| ------ | ------ | ------ |
| GET | [/api/transactions][PlTransaction] | get all transactions |
| GET | [/api/transactions/{id}][PlTransactionId] | get transaction by ID |
| GET | [/api/transactions/_show-image][PlTransactionReport] | show image in byte |
| GET | [/api/transactions/_ocr-this][PlTransactionReport] | request for OCR Service |
| POST | [/api/transactions][PlTransaction] | create a transaction |
| DELETE | [/api/transactions][PlTransaction] | delete all transation |
| DELETE | [/api/transactions/{id}][PlTransactionId] | delete a transaction |

## Get all transactions

- Endpoint : `/api/transactions`
- HTTP Method : `GET`
- Filter : 
    - Support filtering by URL Path, add this in URL: `month=mm&year=yyyy&category=ALL`
    - Default Value:
        - Month : `{today.month}`
        - Year : `{today.year}`
        - Category : `ALL`
- Pagination : 
    - support pagination by URL Path, add this on URL: `page=P&size=S&sortBy=date`
    - Default Value :
        - Page : `0`
        - Size : `5`
        - SortBy : `date`

- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "totalRecords":3,
    "totalPages":2,
    "value": [
        {
            "id": 500000026,
            "type": "PARKING",
            "date":"",
            "imagePath":"user1/transaction1.jpg",
            "price":9000,
            "title":"1st Day Work"
        },
        {
            "id": 500000027,
            "type": "FUEL",
            "date":"",
            "imagePath":"user1/transaction2.jpg",
            "price":90000,
            "amount":23.5,
            "title":"Blibli Future Program"
        }
    ]
}
```

## Get transaction by id
- Endpoint : `/api/transactions/{id}`
- HTTP Method : `GET`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "value": {
        "id": 500000027,
        "category": "FUEL",
        "date":"01:12:2007 03:06:10z",
        "imagePath":"user1/transaction2.jpg",
        "price":90000,
        "amount":23.5,
        "title":"Blibli Future Program"
    }
}
```
- Response Body (Failure) :

```json
{
    "code": 404,
    "status": "NOT_FOUND",
    "errorMessage": {
        "Transaction not Found"
    }
}
```

## Get image
- Endpoint : `api/transactions/_show-image`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
    "imagePath":"1/1040142LHGWLljagbow.jpg"
}
```
- Response body(Success):
*retrieve image in bytes*
- Response body(Failure):


## OCR
- Endpoint : `api/_ocr-this`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
    "imagePath":"1/1040142LHGWLljagbow.jpg"
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "value": {
        "id": 500000026,
        "type": "PARKING",
        "date":"01:12:2007 03:06:10z",
        "price":0,
        "title":null   
    }
}
```
*Note*: the Response body quality depends on the OCR Service response
## Create Transaction
- Endpoint : `api/transactions`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
    "id": 500000026,
    "category": "PARKING",
    "date":"",
    "price":9000,
    "title":"1st Day Work"   
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "value": {
        "id": 500000026,
        "category": "PARKING",
        "date":"01:12:2007 03:06:10z",
        "price":9000,
        "title":"1st Day Work"   
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errorMessage": {
        "price": ["CAN'T BE NULL", "INVALID VALUE"],
        "type":"CAN'T BE NULL",
        "date":"CAN'T BE NULL",
        "title":"CAN'T BE NULL"
    }
}
```

## Upload image

- Endpoint : `/api/transactions/_upload`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
- Request Body :
``` json
{
    "imageValue":"[][]",
}
```
*Note*: ImageValue will be contain the image bytes
- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK"
}
```

## Delete all transactions

- Endpoint : `/api/transactions`
- HTTP Method : `DELETE`
- Request Header : 
    - Accept : `application/json`
- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK"
}
```

## Delete transaction by ID

- Endpoint : `/api/transactions/{id}`
- HTTP Method : `DELETE`
- Request Header : 
    - Accept : `application/json`
- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK"
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
# Transaction
only for authenticated User

# List of API
- [Get All Transactions](#get-all-transactions)
- [Get Transaction by ID](#get-transaction-by-id)
- [Get Image](#get-image)
- [OCR](#ocr)
- [Create Transaction](#create-transaction)
- [Upload Image](#upload-image)
- [Delete Many Transactions](#delete-many-transactions)
- [Delete Transaction by ID](#delete-transactions-by-id)

## Get all transactions

- Endpoint : `/api/transactions`
- HTTP Method : `GET`
- Filter : 
    - Support filtering by URL Path, add this in URL: `month=mm&year=yyyy&category=ALL`
    - Default data:
        - Month : `{today.month}`
        - Year : `{today.year}`
        - Category : `ALL`
- Pagination : 
    - support pagination by URL Path, add this on URL: `page=P&size=S&sortBy=date`
    - Default data :
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
    "paging":{
        "size":10,
        "page":1
    },
    "data": [
        {
            "id": 500000026,
            "type": "PARKING",
            "date":"",
            "image":"user1/transaction1.jpg",
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
    "data": {
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
    "errors": {
        NOT_FOUND
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
    "imagePath":"1/1040142LHGWLljagbow.jpg",
    
}
```
- Response body(Success):
*retrieve image in bytes*

- Response body(Failure):
```json
{
    "code": 404,
    "status": "NOT_FOUND",
    "errors": {
        NOT_FOUND
    }
}
```

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
    "data": {
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
- HTTP Method : `PUT`
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
    "data": {
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
    "errors": {
        "price": [NOT_NULL, INVALID_data],
        "type":NOT_NULL,
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```

## Upload image

- Endpoint : `/api/transactions`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
- Request Body :
``` json
{
    "imagedata":"[][]",
}
```
*Note*: Imagedata will be contain the image bytes
- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK"
}
```

## Delete many transactions

- Endpoint : `/api/transactions`
- HTTP Method : `DELETE`
- Request Header : 
    - Accept : `application/json`
- Request Body:
```json
{
    "ids": [1,2,3,4,5]
}
```
- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK"
}
```

## Delete transactions by id

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

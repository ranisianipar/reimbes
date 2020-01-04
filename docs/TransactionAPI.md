# Transaction
only for authenticated User

# List of API
- [Get All Transactions](#get-all-transactions)
- [Get Transaction by ID](#get-transaction-by-id)
- [Do OCR / Upload Image](#do-ocr)
- [Create Transaction](#create-transaction)
- [Delete Many Transactions](#delete-many-transactions)
- [Delete Transaction by ID](#delete-transactions-by-id)

## Get All Transactions

- Endpoint : `/api/transactions`
- HTTP Method : `GET`
- Filter : 
    - Support filtering by URL Path, add this in URL: `start=[date_format]&end=[date_format]&category=[category_format]&search=***`
    - Default data:
        - start : `NULL`
        - end : `NULL`
        - category : `NULL`
        - search : ``
    - Format data / Possible data value:
        - date : `yyyy-MM-dd'T'HH:mm+07:00`
        - category : `FUEL`, `PARKING`
- Pagination : 
    - support pagination by URL Path, add this on URL: `page=P&size=S&sortBy=created_at`
    - Default data :
        - Page : `1`
        - Size : `5`
        - SortBy : `created_at`

- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -

- Response Body (Success) :

```json
{
  "code": 200,
  "status": "OK",
  "errors": null,
  "paging": {
    "pageNumber": 3,
    "pageSize": 5,
    "totalPages": 3,
    "totalRecords": 1
  },
  "data": [
    {
      "id": 262149,
      "category": "PARKING",
      "userId": 32769,
      "date": "2019-01-01T00:00+07:00",
      "amount": 27000,
      "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
      "title": "HR Team nobar",
      "hours": 6,
      "type": "CAR",
      "location": "GI"
    }
  ],
  "success": true
}
```

## Get Transaction by ID
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
  "errors": null,
  "paging": null,
  "data": {
    "id": 262149,
    "category": "PARKING",
    "userId": 32769,
    "date": "2019-01-01T00:00+07:00",
    "amount": 27000,
    "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
    "title": "HR Team nobar",
    "hours": 6,
    "type": "CAR",
    "location": "GI"
  },
  "success": true
}
```
- Response Body (Failure) :

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "errors": "Not Found => Transaction with ID 249",
  "paging": null,
  "data": null,
  "success": false
}
```

## Do OCR
*upload image*

- Endpoint : `/api/transactions`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
- Request Body :
``` json
{
    "image":"[]",
}
```
*Note*: Imagedata will be contain the image bytes
- Response Body (Success) :

```json
{
  "code": 200,
  "status": "OK",
  "errors": null,
  "paging": null,
  "data": {
    "id": 0,
    "category": "PARKING",
    "userId": 32769,
    "date": "2019-07-25T13:14+07:00",
    "amount": 15000,
    "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
    "title": "gkiih nirga tharrin",
    "hours": 0,
    "type": "CAR",
    "location": null
  },
  "success": true
}
```

## Create Transaction
- Endpoint : `api/transactions`
- HTTP Method : `PUT`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

*PARKING*
```json
{
    "date": "2019-01-01T00:00+07:00",
    "category": "PARKING",
    "userId": 32769,
    "amount": 27000,
    "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
    "title": "HR Team nobar",
    "hours": 6,
    "parkingType": "CAR",
    "location": "GI"
}
```

*FUEL*
```json
{
    "date": "2019-01-01T00:00+07:00",
    "category": "FUEL",
    "userId": 32770,
    "amount": 210000,
    "image": "32770/5e0cb09c-5e45-43a9-af7f-5e6ff9726a70.png",
    "title": "Jalan-jalan",
    "liters": 21,
    "fuelType": "PREMIUM"
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
        "date":"YYYY-MM-DDTHH:mm:ss.sssZ",
        "price":9000,
        "title":"1st Day Work",
        "created_at":1559058600
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
  "status": "OK",
  "errors": null,
  "paging": null,
  "data": null,
  "success": true
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
  "status": "OK",
  "errors": null,
  "paging": null,
  "data": null,
  "success": true
}
```



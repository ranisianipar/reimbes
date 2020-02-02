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
          "id": 98305,
          "category": "FUEL",
          "userId": 3,
          "date": 1565281355000,
          "amount": 100000,
          "attachments": [
            "/storage/3/transaction/b114928a-adbd-4dc3-85ef-14664b4bbc8b.jpeg"
          ],
          "title": "Nyoba bensin pake atribut baru",
          "liters": 17.0,
          "kilometers": 11,
          "type": "SOLAR"
    },
    {
          "id": 32768,
          "category": "PARKING",
          "userId": 3,
          "date": 1565281355000,
          "amount": 120000,
          "attachments": [
            "/storage/3/transaction/f7b4498e-a410-4576-b5ac-f0011fb2be3a.jpeg"
          ],
          "title": "Main-main ke WTC"
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
    "date": 1565281355000,
    "amount": 27000,
    "attachments": ["/storage/3/transaction/59d164b1-0aa5-4f48-8ea9-7ad703809a4f.png"],
    "title": "HR Team nobar",
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
    "category": "PARKING",
	"attachments":[
		"data:image/png;base64,iVBORw..."
	]
}
```
*Note*: The `attachments` attribute contains ONLY 1 image in base64 (string) format
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
    "date": 1565281355000,
    "amount": 15000,
    "attachments": ["/storage/3/transaction/59d164b1-0aa5-4f48-8ea9-7ad703809a4f.png"],
    "title": "gkiih nirga tharrin",
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
    "date": 1565281355000,
    "category": "PARKING",
    "userId": 32769,
    "amount": 27000,
    "attachments": ["/storage/3/transaction/59d164b1-0aa5-4f48-8ea9-7ad703809a4f.png"],
    "title": "HR Team nobar",
    "location": "GI"
}
```

*FUEL*
```json
{
	"date": 1565281355000,
	"category": "FUEL",
    "amount": 100000,
    "attachments": ["/storage/3/transaction/b114928a-adbd-4dc3-85ef-14664b4bbc8b.jpeg"],
    "title": "Nyoba bensin pake atribut baru",
	"liters":17,
	"kilometers":11,
	"fuelType":"SOLAR",
    "location": "dimana ya"
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
        "date":1565281355000,
        "amount":9000,
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



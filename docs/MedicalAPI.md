
# Medical

# List of API
- [Get All Medicals](#get-all-medicals)
- [Get Medical by ID](#get-medical-by-id)
- [Create Medical](#create-medical)
- [Delete Medical](#delete-medical)
- [Update Medical](#update-medical)

## Get All Medicals
only for USER

- Endpoint : `/api/medicals`
- HTTP Method : `GET`
- Filter : 
    - Format data / Possible data value:
        - search: "`Demam`"
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
  "paging": null,
  "data": [
    {
      "id": 1,
      "title": "pusyang",
      "age": 21,
      "amount": 700000,
      "date": 1575775787000,
      "patient": {
        "id": 4,
        "name": "Clean Bandit",
        "dateOfBirth": "2000-11-19"
      },
      "attachments": []
    }
  ],
  "success": true
}
```

## Get Medical by ID
- Endpoint : `/api/medicals/{id}`
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
    "id": 1,
    "title": "pusyang",
    "age": 21,
    "amount": 700000,
    "date": 1575775787000,
    "patient": {
      "id": 4,
      "name": "Clean Bandit",
      "dateOfBirth": "2000-11-19"
    },
    "attachments": []
  },
  "success": true
}
```
- Response Body (Failure) :

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "errors": "Not Found => Medical with ID {ID}",
  "paging": null,
  "data": null,
  "success": false
}
```

## Create Medical
- Endpoint : `api/medicals`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 
```json
{
	"title":"Istri Demam"
	"date":`2020-01-01`,
	"amount":1000000,
	"attachment":[
		"%%image_in_base64%%",
		...
	],
	"patient": null
}
```
*patient: null => assign Current User as the Patient*
- Response Body (Success) :

```json
{
  "code": 200,
  "status": "OK",
  "errors": null,
  "paging": null,
  "data": {
    "id": 14,
    "title": "pusyang",
    "age": 21,
    "amount": 700000,
    "date": 1575775787000,
    "patient": {
      "id": 32768,
      "name": "user-with-member",
      "dateOfBirth": 895017600000
    },
    "attachments": [
        "92769/lqeigbhqohjgpoq313019504185.jpg",
        "92769/1gr2hbo23gbfo12332r5m5.jpg"
    ]
  },
  "success": true
}
```
- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "amount": [NOT_NULL, INVALID_DATA],
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```
## Delete Medical

- Endpoint : `/api/medicals/{id}`
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
- Response Body (Failure) : -

## Update Medical
- Endpoint : `api/medicals/{id}`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 
```json
{
	"title": "pusyang update",
	"age": 21,
	"amount": 710000,
	"date": 1575775787000,
	"patient": {
        "id": 4,
        "name": "Clean Bandit",
		"dateOfBirth": "2000-11-19"
	},
	"attachments": null
}
```

- Response Body (Success) :

```json
{
  "code": 200,
  "status": "OK",
  "errors": null,
  "paging": null,
  "data": [
    {
      "id": 1,
      "title": "pusyang",
      "age": 21,
      "amount": 700000,
      "date": 1575775787000,
      "patient": {
        "id": 4,
        "name": "Clean Bandit",
        "dateOfBirth": "2000-11-19"
      },
      "attachments": []
    }
  ],
  "success": true
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "amount": [NOT_NULL, INVALID_DATA],
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```


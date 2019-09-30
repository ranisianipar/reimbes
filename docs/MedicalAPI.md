
# Medical
only for authenticated User

# List of API
- [Get All Medicals](#get-all-medicals)
- [Get Medical by ID](#get-medical-by-id)
- [Create Medical](#create-medical)
- [Delete Medical by ID](#delete-medical-by-id)

## Get All Medicals

- Endpoint : `/api/medicals`
- HTTP Method : `GET`
- Filter : 
    - Support filtering by URL Path, add this in URL: `start=[date_format]&end=[date_format]&search=***`
    - Default data:
        - start: `NULL`
        - end: `NULL`
        - search: ``
    - Format data / Possible data value:
        - date: `yyyy-MM-dd'T'HH:mm+07:00`
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
  "paging": {
    "pageNumber": 3,
    "pageSize": 5,
    "totalPages": 3,
    "totalRecords": 1
  },
  "data": [
    {
      "id": 262149,
      "userId": 32769,
      "date": "2019-01-01T00:00+07:00",
      "amount": 27000,
      "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
      "title": "Demam",
      "patient": {
		"id":92769,
		"name":"Andre Forbes"
		"relationship":`CHILDREN`,
		"dateOfBirth":"1993-01-01"
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
  "paging": {
    "pageNumber": 3,
    "pageSize": 5,
    "totalPages": 3,
    "totalRecords": 1
  },
  "data": {
      "id": 262149,
      "userId": 32769,
      "date": "2019-01-01T00:00+07:00",
      "amount": 27000,
      "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
      "title": "Demam",
      "patient": {
		"id":92769,
		"name":"Andre Forbes"
		"relationship":`CHILDREN`,
		"dateOfBirth":"1993-01-01"
	    }
    }
  
```
- Response Body (Failure) :

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "errors": "Not Found => Medical with ID 249",
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
    "date": "2019-01-01T00:00+07:00",
    "userId": 32769,
    "amount": 27000,
    "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
    "title": "Demam",
    // this attribute will be null when User claim for himself
    "patient": {
		"id":92769,
		"name":"Andre Forbes"
		"relationship":`CHILDREN`,
		"dateOfBirth":"1993-01-01"
	}
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "data": {
        "id": 500000026,
        "date":"YYYY-MM-DDTHH:mm:ss.sssZ",
        "amount":9000,
        "title":"Demam",
        "created_at":1559058600,
        "image": "32769/5e0cb09c-5e45-43a9-af7f-5e6ff9726a28.png",
        "title": "Demam",
        // this attribute will be null when User claim for himself
        "patient": {
    		"id":92769,
    		"name":"Andre Forbes"
    		"relationship":`CHILDREN`,
    		"dateOfBirth":"1993-01-01"
    	}
    }
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
## Delete Medical by id

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


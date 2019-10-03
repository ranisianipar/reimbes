
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
  "paging": {
    "pageNumber": 3,
    "pageSize": 5,
    "totalPages": 3,
    "totalRecords": 1
  },
  "data": [
    {
		"id":92769,
		"title":"Istri Demam",
		"amount":1000000,
		"date":`2020-01-01`,
		"attachment":[
		// need notes?
    		"92769/lqeigbhqohjgpoq313019504185.jpg",
    		"92769/1gr2hbo23gbfo12332r5m5.jpg"
		],
		"patient":{
    		"id":92768,
    		"name":"Zendaya"
    		"relationship":`SPOUSE`,
    		"dateOfBirth":"1979-02-06"
		}
	},
	{
	// sama seperti data di atas
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
		"id":92769,
		"title":"Istri Demam",
		"amount":1000000,
		"date":`2020-01-01`,
		"attachment":[
		// need notes?
    		"92769/lqeigbhqohjgpoq313019504185.jpg",
    		"92769/1gr2hbo23gbfo12332r5m5.jpg"
		]
		"patient":{
    		"id":92768,
    		"name":"Zendaya"
    		"relationship":`SPOUSE`,
    		"dateOfBirth":"1979-02-06"
		}
	}
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
	// need notes?
		"92769/lqeigbhqohjgpoq313019504185.jpg",
		"92769/1gr2hbo23gbfo12332r5m5.jpg"
	],
	"patient":{
		"id":92768,
		"name":"Zendaya"
		"relationship":`SPOUSE`,
		"dateOfBirth":"1979-02-06"
	}
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "data": {
        "id":"998175",
    	"title":"Istri Demam"
    	"amount":1000000,
    	"date":`2020-01-01`,
    	"attachment":[
    	// need notes?
    		"92769/lqeigbhqohjgpoq313019504185.jpg",
    		"92769/1gr2hbo23gbfo12332r5m5.jpg"
    	],
    	"patient":{
    		"id":92768,
    		"name":"Zendaya"
    		"relationship":`SPOUSE`,
    		"dateOfBirth":"1979-02-06"
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
    "id":"998175",
	"title":"Istri Demam"
	"amount":1000000,
	"date":`2020-01-01`,
	"attachment":[
	// need notes?
		"92769/lqeigbhqohjgpoq313019504185.jpg",
		"92769/1gr2hbo23gbfo12332r5m5.jpg"
	],
	"patient":{
		"id":92768,
		"name":"Zendaya"
		"relationship":`SPOUSE`,
		"dateOfBirth":"1979-02-06"
	}
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "data": {
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
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "amount": [NOT_NULL, INVALID_DATA],
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```


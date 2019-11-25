
# Family Member

List of API
- [Get All Family Member](#get-all-family-member)
- [Get Family Member by ID](#get-family-member-by-id)
- [Create Family Member](#create-family-member)
- [Delete Family Member](#delete-family-member-by-id)
- [Update Family Member](#update-family-member)

## Get All Family Member

- Endpoint : `/api/family-members`
- HTTP Method : `GET`
- Filter : 
    - Format data / Possible data value:
        - search: "Supatno"
        - user-id: 198
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
    "totalRecords": 2
  },
  "data": [
    {
		"id":92769,
		"name":"Andre Forbes"
		"relationship":`CHILDREN`,
		"dateOfBirth":"1993-01-01"
	},
	{
		"id":92768,
		"name":"Zendaya"
		"relationship":`SPOUSE`,
		"dateOfBirth":"1979-02-06"
	}
  ],
  "success": true
}
```
## Get Family Member by ID
- Endpoint : `/api/family-members/{familyMemberId}`
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
		"name":"Andre Forbes"
		"relationship":`CHILDREN`,
		"dateOfBirth":"1993-01-01"
    }
  
```
- Response Body (Failure) :

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "errors": "Not Found => Family Member with ID 249",
  "paging": null,
  "data": null,
  "success": false
}
```

## Create Family Member
this method to register a new family member to a MALE USER.
- Endpoint : `api/family-members?user-id={userid}`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 
```json
{
	"id":92769,
	"name":"Andre Forbes"
	"relationship":`CHILDREN`,
	"dateOfBirth":"1993-01-01"
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
    "code": 500,
    "status": "BAD_REQUEST",
    "errors": "Data Constraint => GENDER_CONSTRAINT"
}
```

## Delete Family Member by id

- Endpoint : `/api/family-members/{id}`
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

## Update Family Member
- Endpoint : `api/family-members/{familyMemberId}`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 
```json
{
	"id":92769,
	"name":"Andre Forbes"
	"relationship":`CHILDREN`,
	"dateOfBirth":"1993-01-01"
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
    "code": 500,
    "status": "BAD_REQUEST",
    "errors": {
        "amount": [NOT_NULL, INVALID_DATA],
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```

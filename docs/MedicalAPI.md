
# Family Member

# List of API

### Admin
- [Get All Family Member for Admin](#get-all-family-member-for-admin)
- [Get Family Member by ID](#get-family-member-by-id-for-admin)
- [Create Family Member](#create-family-member-for-admin)
- [Delete Family Member](#delete-family-member-for-admin)
- [Update Family Member](#update-family-member-for-admin)
### User
- [Get All Family Member](#get-all-family-member)
- [Get Family Member by ID](#get-family-member-by-id)


## Get All Family Member for Admin
only for ADMIN

- Endpoint : `/api/admin/user/{userId}/family-members`
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

## Get Family Member by ID for Admin
- Endpoint : `/api/admin/user/{userId}/family-members/{familyMemberId}`
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

## Create Medical
- Endpoint : `api/admin/user/{userId}/family-members`
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
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "amount": [NOT_NULL, INVALID_DATA],
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```
## Delete Family Member by id

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

## Update Family Member
- Endpoint : `api/admin/user/{userId}/family-members/{familyMemberId}`
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
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "amount": [NOT_NULL, INVALID_DATA],
        "date":NOT_NULL,
        "title":NOT_NULL
    }
}
```

## Get All Family Member
only for ADMIN

- Endpoint : `/api/family-members`
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


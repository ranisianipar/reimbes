
# Family Member

List of API
- [Get All Family Member](#get-all-family-member)
- [Get Family Member by ID](#get-family-member-by-id)
- [Create Family Member](#create-family-member)
- [Delete Family Member](#delete-family-member-by-id)
- [Update Family Member](#update-family-member)

## Get All Family Member

- Endpoint : [USER] `/api/family-members` | [ADMIN] `/api/admin/family-members`
- HTTP Method : `GET`
- Filter : 
    - Format data / Possible data value:
        - search: "Supatno"
        - user-id: 198 {QUESTIONABLE}
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
    "pageNumber": 1,
    "pageSize": 10,
    "totalPages": 1,
    "totalRecords": 3
  },
  "data": [
    {
        "id": 4,
        "name": "Isyana Sarasvati",
        "familyMemberOf": "3",
        "relationship": "CHILDREN",
        "dateOfBirth": 820368000000
    },

    ...

  ]
```
## Get Family Member by ID
- Endpoint : [USER] `/api/family-members/{familyMemberId}` | [ADMIN]`/api/admin/family-members/{familyMemberId}`
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
       "id": 4,
       "name": "Isyana Sarasvati",
       "familyMemberOf": "3",
       "relationship": "CHILDREN",
       "dateOfBirth": 820368000000
  },
  "success": true
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
This method is only for admin.
- Endpoint : `api/admin/family-members?user-id={userid}`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 
```json
{
	"name":"Isyana Sarasvati",
	"relationship":"CHILDREN",
	"dateOfBirth":"1995-12-31"
}
```

- Response Body (Success) :

```json
{
  "code": 200,
  "status": "OK",
  "errors": null,
  "paging": null,
  "data": {
        "id": 4,
        "name": "Isyana Sarasvati",
        "familyMemberOf": "3",
        "relationship": "CHILDREN",
        "dateOfBirth": 820368000000
  },
  "success": true
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
This method is only for admin.
- Endpoint : `/api/admin/family-members/{id}`
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
This method is only for admin.
- Endpoint : `api/admin/family-members/{familyMemberId}?user-id={userId}`
*Note: Include user-id query if only if Reims User of Family Member need to be update*
- HTTP Method : `PUT`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 
```json
{
	"name":"Andre Forbes",
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
        "familyMemberOf": "3",
        "relationship": "CHILDREN",
        "dateOfBirth": 820368000000
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


# List of API
- [Get All users](#get-all-users)
- [Get user by ID](#get-user-by-id)
- [Create User](#create-user)
- [Update user](#update-user)
- [Logout user](#logout-user)
- [Remove all users](#delete-all-users)
- [Remove user by ID](#delete-user-by-id)

## Get all users

- Endpoint : `/api/admin/users`
- HTTP Method : `GET`
- Filter :
    - Support filtering by URL Path, add this on URL :
        `search=NAMAORANG`
    - Default data :
       - search : ``
- Pagination : 
    - support pagination by URL Path, add this on URL: `page=P&size=S&sortBy=updated_at`
    - Default data :
        - pageNumber : `0`
        - pageSize : `5`
        - SortBy : `updated_at`

- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`

- Request Body : -

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "paging":{
            "pageSize":10,
            "pageNumber":1,
            "totalRecords":100,
            "totalPages":10
     },
    "data": [
        {
            "id": 500000026,
            "username": "hafiztesting",
            "role": "ADMIN",
            "created_at":1559058600
        },
        {
            "id": 500000027,
            "username": "testinggg",
            "role": "USER",
            "created_at":1559058600
        }
    ]
}
```

- Response Body (Failure) : -

## Get user by id
- Endpoint : `/api/admin/users/{id}`
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
    "pagination":{
        "totalPage":10,
        "page":1
    },
    "data": {
        "id": 500000026,
        "username": "hafiztesting",
        "role": "ADMIN",
        "created_at":1559058600
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

## Create user
- Endpoint : `api/admin/users`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345",
	"role": "ADMIN"
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "data": {
        "id": 1,
        "username": "eko1@pyeongyang.com",
        "role": "ADMIN",
        "created_at":1559058600,
        "updated_at":0
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "password": NOT_NULL,
        "username": NOT_UNIQUE,
        "role": INVALID_VALUE
    }
}
```

## Update user
- Endpoint : `api/admin/users/{id}`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345",
	"role": "USER"
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "data": {
        "id": 1,
        "username": "eko1@pyeongyang.com",
        "role": "USER",
        "created_at":1559058600,
        "updated_at":1559058655"
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": {
        "password": NOT_NULL,
        "username": INVALID_VALUE
    }
}
```

## Login user

- Endpoint : `/api/login`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
   "username" : "hafiztesting@mailinator.com",
   "password" : "test123"
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "data": {
        "id": 500000026,
        "username": "hafiztesting@mailinator.com"
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errors": NOT_MATCH
}
```

## Logout user

- Endpoint : `/api/logout`
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

## Delete all users

- Endpoint : `/api/admin/users`
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

## Delete user by ID

- Endpoint : `/api/admin/users/{id}`
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

  

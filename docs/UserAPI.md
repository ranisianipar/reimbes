# Users
- [Create User](#create-user)
- [Get All Users](#get-all-users)
- [Get Personal Data](#get-personal-data)
- [Get Personal Report](#get-personal-report)
- [Get User by ID](#get-user-by-id)
- [Logout User](#logout-user)
- [Remove All Users](#delete-all-users)
- [Remove User by ID](#delete-user-by-id)
- [Update User](#update-user)
- [Update Personal Data](#update-personal-data)
- [Get Image](#get-image)

## Get Personal Report
- Endpoint : `/api/users/report`
- HTTP Method : `GET`
- Allowed User: `Authorized User`
- Filter :
    - Support filtering by URL Path, add this on URL :
        `start=[date in epoch format]&end=[date in epoch format]`
    - Default data:
        - start = 0
        - end = 0
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -
- Response Body (Success) : -
- Response Body (Failure) : -

## Get Personal Data
- Endpoint : `/api/users`
- HTTP Method : `GET`
- Allowed User: `Authorized User`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "pagination":null,
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

## Get all
- Endpoint : `/api/admin/users`
- HTTP Method : `GET`
- Allowed User: `ADMIN`
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
  "errors": null,
  "paging": {
    "pageNumber": 1,
    "pageSize": 10,
    "totalPages": 1,
    "totalRecords": 2
  },
  "data": [
    {
      "id": 3,
      "username": "user-with-member",
      "role": "USER",
      "license": null,
      "vehicle": null,
      "division": null,
      "dateOfBirth": "1998-05-13",
      "gender": "MALE"
    },
    {
      "id": 2,
      "username": "user-no-member",
      "role": "USER",
      "license": null,
      "vehicle": null,
      "division": null,
      "dateOfBirth": "1998-05-13",
      "gender": "FEMALE"
    }
  ],
  "success": true
}
```

- Response Body (Failure) : -

## Get user by id
- Endpoint : `/api/admin/users/{id}`
- HTTP Method : `GET`
- Allowed User: `ADMIN`
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
    "id": 3,
    "username": "user-with-member",
    "role": "USER",
    "license": null,
    "vehicle": null,
    "division": null,
    "dateOfBirth": "1998-05-13",
    "gender": "MALE"
  },
  "success": true
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
- Allowed User: `ADMIN`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body :

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345",
	"role": "USER",
	"gender":"MALE",
	"dateOfBirth":"1970-11-1",
	"license":"B XXXX XX",
	"vehicle":"CAR"
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
	    "gender":"MALE",
	    "dateOfBirth":"1970-11-1",
	    "license":"B XXXX XX",
	    "vehicle":"CAR"
        "created_at":1559058600,
        "updated_at":0
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 500,
    "status": "BAD_REQUEST",
    "errors": {
        "password": NOT_NULL,
        "username": NOT_UNIQUE,
        "role": INVALID_VALUE
    }
}
```

## Login user

- Endpoint : `/api/login`
- HTTP Method : `POST`
- Allowed User: `PUBLIC`
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
    "code": 500,
    "status": "BAD_REQUEST",
    "errors": NOT_MATCH
}
```

## Logout user

- Endpoint : `/api/logout`
- HTTP Method : `DELETE`
- Allowed User: `PUBLIC`
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
- Allowed User: `ADMIN`
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
- Allowed User: `ADMIN`
- Request Header : 
    - Accept : `application/json`
- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK"
}
```

## Update user
- Endpoint : `api/admin/users/{id}`
- HTTP Method : `POST`
- Allowed User: `ADMIN`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body :

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345",
	"role": "USER",
	"gender":"MALE",
	"dateOfBirth":"1970-11-1",
	"license":"B XXXX XX",
	"vehicle":"CAR"
    "created_at":1559058600,
    "updated_at":0
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
        "gender":"MALE",
        "dateOfBirth":"1970-11-1",
        "license":"B XXXX XX",
        "vehicle":"CAR",
        "created_at":1559058600,
        "updated_at":1559058655
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 500,
    "status": "BAD_REQUEST",
    "errors": {
        "password": NOT_NULL,
        "username": INVALID_VALUE
    }
}
```

## Update Personal Data
- Endpoint : `/api/users`
- HTTP Method : `PUT`
- Allowed User: `Authorized User`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body :

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345",
	"role": "USER",
	"gender":"MALE",
	"dateOfBirth":"1970-11-1",
	"license":"B XXXX XX",
	"vehicle":"CAR"
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
        "gender":"MALE",
        "dateOfBirth":"1970-11-1",
        "license":"B XXXX XX",
        "vehicle":"CAR",
        "created_at":1559058600,
        "updated_at":1559058655
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 500,
    "status": "BAD_REQUEST",
    "errors": {
        "password": NOT_NULL,
        "username": INVALID_VALUE
    }
}
```

## Get Personal Report
- Endpoint : `/api/users/report`
- HTTP Method : `GET`
- Allowed User: `Authorized User`
- Filter :
    - Support filtering by URL Path, add this on URL :
        `start=[date in epoch format]&end=[date in epoch format]`
    - Default data:
        - start = 0
        - end = 0
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -
- Response Body (Success) : -
- Response Body (Failure) : -

## Get Image
- Endpoint : `/api/users/image?path={imagePath}`
- HTTP Method : `GET`
- Allowed User: `Authorized User`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -

- Response Body (Success) : image in array of bytes
- Response Header (Success) :
    - Content-Type : [`image/jpeg`, `image/jpg`, `image/png`]

- Response Body (Failure) : []
```
  

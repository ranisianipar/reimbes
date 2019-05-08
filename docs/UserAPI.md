# List of API
- [Get All users](#get-all-users)
- [Get user by ID](#get-user-by-id)
- [Create User](#create-user)
- [Update user](#update-user)
- [Logout user](#logout-user)
- [Remove all users](#delete-all-users)
- [Remove user by ID](#delete-user-by-id)

## Get all users

- Endpoint : `/api/users`
- HTTP Method : `GET`
- Pagination : 
    - support pagination by URL Path, add this on URL: `page=P&size=S&sortBy=date`
    - Default Value :
        - Page : `0`
        - Size : `5`
        - SortBy : `date`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : -

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "value": [{
        "id": 500000026,
        "username": "hafiztesting@mailinator.com"
        },
        {
        "id": 500000027,
        "username": "testinggg@mailinator.com"
        }
    ]
}
```
<!-- BELOM TAU -->

- Response Body (Failure) : -

## Get user by id
- Endpoint : `/api/users/{id}`
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
    "value": {
        "id": 500000026,
        "username": "hafiztesting@mailinator.com"
    }

}
```
- Response Body (Failure) :

```json
{
    "code": 404,
    "status": "NOT_FOUND",
    "errorMessage": {
        "User not Found"
    }
}
```

## Create user
- Endpoint : `api/users`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345"
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "value": {
        "id": 1,
        "username": "eko1@pyeongyang.com"
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errorMessage": {
        "password": "Password can't be blank",
        "username": "Invalid Username"
    }
}
```

## Update user
- Endpoint : `api/users/{id}`
- HTTP Method : `POST`
- Request Header : 
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body : 

```json
{
	"username" : "eko1@pyeongyang.com",
	"password" : "blibli12345"
}
```

- Response Body (Success) :

```json
{
    "code": 200,
    "status": "OK",
    "value": {
        "id": 1,
        "username": "eko1@pyeongyang.com"
    }
}
```

- Response Body (Failure) :

```json
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errorMessage": {
        "password": "Password can't be blank",
        "username": "Invalid Username"
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
    "errorMessage": "Password and Username don't match"
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

- Endpoint : `/api/users`
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

- Endpoint : `/api/users/{id}`
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

  

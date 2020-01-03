
# Admin
Any URL that can be accessed by Admin

# List of API
- [Get All Medicals](#get-all-medicals)
- [Get Medical by ID](#get-medical-by-id)
- [Get All Users](#get-all-users)
- [Get User by ID](#get-user-by-id)
- [Create User](#create-user)
- [Delete User](#delete-user)

## Get All Medicals
only for USER

- Endpoint : `/api/admin/medicals`
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
- Endpoint : `/api/admin/medicals/{id}`
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

## Create User
- Endpoint : `api/admin/users`
- HTTP Method : `POST`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body :
```json
{
	`COMING SOON`
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
## Delete User

- Endpoint : `/api/admin/users/{id}`
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

## Update Users
- Endpoint : `api/admin/users/{id}`
- HTTP Method : `POST`
- Request Header :
    - Accept : `application/json`
    - Content-Type : `application/json`
- Request Body :
```json
{
	`COMING SOON`
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



# Backend API Documentation

### `/api/login`
- Purpose: Authorise the user to access the service.
- Method: POST
- Request: POST form with the following key-value pairs:
  - email: <valid-email>
  - password: <user-password>
- Response: Status code 200 and a json body with the following key-value pairs:
  - "success": boolean
    - Notifies whether the requests succeeded or not.
  - "error": string
    - If the request did not succeed, this value holds and error message to display to the user.

### `/api/register`
- Purpose: Create a new user account
- Method: POST
- Request: POST form with the following key-value pairs:
    - display_name: <users-display-name>
    - password: <user-password>
    - email: <valid-email>
- Response: Status code 200 and a json body with the following key-value pairs:
    - "success": boolean
        - Notifies whether the requests succeeded or not.
    - "error": string
        - If the request did not succeed, this value holds and error message to display to the user.

## Internal
These api's are for debugging / internal / testing purposes and should not be available
to the outside world.

### `/api/health_check`

- Purpose: Check if the backend api is receiving requests
- Method: GET
- Response: returns code 200 with body "GOOD" if good, non 200 status code if not receiving requests.
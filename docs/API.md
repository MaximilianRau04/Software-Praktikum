# API Documentation

## EXCHANGE-DAYS

### Endpoints

- **GET** `/api/exchange-days`  
  Retrieves all exchange days.

- **GET** `/api/exchange-days/{id}`  
  Retrieves a specific exchange day by its ID.

- **GET** `/api/exchange-days/{id}/events`  
  Retrieves all events under a specific exchange day.

### Methods
- **POST, PUT, DELETE**  
  Defaults apply for CRUD operations.

---

## EVENTS

### Endpoints

- **GET** `/api/events`  
  Retrieves all events.

- **GET** `/api/events/{id}`  
  Retrieves a specific event by its ID.

- **GET** `/api/events/{id}/registeredUsers`  
  Retrieves all registered users for a specific event.

- **GET** `/api/events/{id}/organizer`  
  Retrieves the organizer (as a User) of the specified event.

- **GET** `/api/events/{id}/exchange-day`  
  Retrieves the exchange day to which the event belongs.

- **GET** `/api/events/{id}/feedback`  
  Retrieves all feedback given for a specific event.

- **GET** `/api/events/{id}/qr-code`  
  Downloads the QR code for the event as a `.png` file.

### Additional Methods
- **POST** `/api/events/{id}/attendance`  
  Confirms attendance after scanning the QR code.

- **POST, DELETE**  
  Default expected implementations for creating and deleting feedback.

- **PUT**  
  TODO (implementation in progress).

---

## FEEDBACK

### Endpoints

- **GET** `/api/feedback`  
  Retrieves all feedback.

- **GET** `/api/feedback/{id}`  
  Retrieves a specific feedback entry by its ID.

- **GET** `/api/feedback/{id}/author`  
  Retrieves the author of the feedback (if not anonymous).

### Methods
- **POST, DELETE**  
  Defaults apply for CRUD operations.

- **PUT**  
  TODO (implementation in progress).

---

## USERS

### Endpoints

- **GET** `/api/users`  
  Retrieves all users.

- **GET** `/api/users/{id}`  
  Retrieves a specific user by their ID.

- **GET** `/api/users/search?username=`  
  Retrieves a user by their username (query parameter).

- **GET** `/api/users/{id}/registeredEvents`  
  Retrieves all events the user is registered for.

- **GET** `/api/users/{id}/feedback`  
  Retrieves all feedback given by the user.

- **GET** `/api/users/{id}/participations`  
  Retrieves all attendances of the user, both confirmed and unconfirmed.

### Additional Methods

- **POST** `/api/users`  
  Default for creating a new user.

- **PUT** `/api/users/{id}`  
  Default for updating user information.

- **DELETE** `/api/users/{id}`  
  Default for deleting a user.

- **POST** `/api/users/{id}/eventRegistration?eventId=`  
  Registers a user to an event.

- **DELETE** `/api/users/{id}/eventRegistration?eventId=`  
  Removes a user from an event.

---

### Notes

- All endpoints return JSON responses.
- Validation errors and exceptions are handled via appropriate HTTP status codes.
- Authentication and authorization details are not yet relevant.
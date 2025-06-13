# Smart Health API

A Spring Boot-based REST API for managing health and fitness data, including user profiles, activities, food logs, and cycle tracking.

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup

1. Clone the repository
2. Configure MySQL database:
   ```sql
   CREATE DATABASE smart_health;
   ```
3. Update `application.properties` with your MySQL credentials
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

### User Profile

#### Get User Profile
```http
GET /api
Authorization: Bearer <token>
```

#### Update User Profile
```http
PUT /api
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Updated Name",
    "gender": "MALE",
    "dateOfBirth": "1990-01-01T00:00:00",
    "height": 180,
    "heightUnit": "cm",
    "weightUnit": "kg"
}
```

### Goals

#### Get User Goals
```http
GET /api/goals
Authorization: Bearer <token>
```

#### Create Goal
```http
POST /api/goals
Authorization: Bearer <token>
Content-Type: application/json

{
    "goalType": "WEIGHT_LOSS",
    "targetValue": 70,
    "startDate": "2024-03-20T00:00:00",
    "endDate": "2024-06-20T00:00:00",
    "status": "IN_PROGRESS"
}
```

#### Update Goal
```http
PUT /api/goals/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
    "goalType": "WEIGHT_LOSS",
    "targetValue": 65,
    "status": "COMPLETED"
}
```

#### Delete Goal
```http
DELETE /api/goals/{goalId}
Authorization: Bearer <token>
```

### Activities

#### Get All Activities
```http
GET /api/activities
Authorization: Bearer <token>
```

#### Create Activity
```http
POST /api/activities
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Running",
    "description": "Outdoor running",
    "caloriesBurnedPerHour": 600,
    "category": "CARDIO",
    "intensity": "HIGH"
}
```

#### Update Activity
```http
PUT /api/activities/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Running",
    "description": "Updated description",
    "caloriesBurnedPerHour": 650
}
```

#### Delete Activity
```http
DELETE /api/activities/{id}
Authorization: Bearer <token>
```

### Activity Logs

#### Get All Activity Logs
```http
GET /api/activity-logs
Authorization: Bearer <token>
```

#### Get User Activity Logs
```http
GET /api/activity-logs/user/{userId}
Authorization: Bearer <token>
```

#### Create Activity Log
```http
POST /api/activity-logs
Authorization: Bearer <token>
Content-Type: application/json

{
    "activity": {
        "id": 1
    },
    "startTime": "2024-03-20T10:00:00",
    "endTime": "2024-03-20T11:00:00",
    "duration": 60,
    "caloriesBurned": 600,
    "notes": "Morning run"
}
```

### Food

#### Get All Foods
```http
GET /api/foods
Authorization: Bearer <token>
```

#### Create Food
```http
POST /api/foods
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Apple",
    "calories": 95,
    "protein": 0.5,
    "carbs": 25,
    "fat": 0.3,
    "servingSize": 1,
    "servingUnit": "medium"
}
```

### Food Logs

#### Get All Food Logs
```http
GET /api/food-logs
Authorization: Bearer <token>
```

#### Get User Food Logs
```http
GET /api/food-logs/user/{userId}
Authorization: Bearer <token>
```

#### Create Food Log
```http
POST /api/food-logs
Authorization: Bearer <token>
Content-Type: application/json

{
    "food": {
        "id": 1
    },
    "timestamp": "2024-03-20T12:00:00",
    "quantity": 1,
    "mealType": "LUNCH",
    "notes": "Healthy lunch"
}
```

### Cycle Data

#### Get All Cycle Data
```http
GET /api/cycle-data
Authorization: Bearer <token>
```

#### Get User Cycle Data
```http
GET /api/cycle-data/user/{userId}
Authorization: Bearer <token>
```

#### Create Cycle Data
```http
POST /api/cycle-data
Authorization: Bearer <token>
Content-Type: application/json

{
    "startDate": "2024-03-01",
    "endDate": "2024-03-07",
    "phase": "MENSTRUAL",
    "symptoms": ["CRAMPS", "FATIGUE"],
    "mood": "TIRED",
    "notes": "Regular cycle"
}
```

## Testing the API

### Using cURL

1. Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User"
  }'
```

2. Login to get JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

3. Use the token for authenticated requests:
```bash
curl -X GET http://localhost:8080/api/goals \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Using Postman

1. Import the following collection:
```json
{
  "info": {
    "name": "Smart Health API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/register",
            "body": {
              "mode": "raw",
              "raw": "{\n    \"email\": \"test@example.com\",\n    \"password\": \"password123\",\n    \"name\": \"Test User\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/login",
            "body": {
              "mode": "raw",
              "raw": "{\n    \"email\": \"test@example.com\",\n    \"password\": \"password123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            }
          }
        }
      ]
    }
  ]
}
```

2. Set up environment variables:
   - `base_url`: http://localhost:8080
   - `token`: (JWT token from login response)

## Error Handling

The API uses standard HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

Error responses include:
```json
{
    "error": "Error Type",
    "message": "Detailed error message"
}
```

## Security

- JWT-based authentication
- Password encryption using BCrypt
- CORS enabled for all origins
- CSRF protection disabled for API endpoints

## Database Schema

The application uses the following main tables:
- users
- user_goals
- activities
- activity_logs
- foods
- food_logs
- cycle_data

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request 
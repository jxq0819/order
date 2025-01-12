# Order Service

This is a Spring Boot application for orders REST API. Below are the instructions to run the application and the available endpoints.

## Prerequisites

- Docker
- Docker Compose
- Git Bash or any terminal that supports shell scripts (for Windows users)
- Google Maps API Key - this has been provided in the `application.properties` file

## Running the Application
Clarification: The application JAR file is already built by Maven manually and included in the root directory of the repository. The application can be run using Docker Compose in the `start.sh` script directly.

1. Clone the repository:
    ```
    git clone <repository-url>
    cd <repository-directory>
    ```

2. Make the `start.sh` script executable:
    ```
    chmod +x start.sh
    ```

3. Run the `start.sh` script to start the application:
    ```
    ./start.sh
    ```

This script will navigate to the directory containing the `compose.yml` file and run `docker-compose up -d` to start the services in detached mode.

## Available Endpoints

### Create Order

- **URL:** `/orders`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "origin": ["origin_latitude", "origin_longitude"],
        "destination": ["destination_latitude", "destination_longitude"]
    }
    ```
- **Response:**
  - `201 Created` with the order ID, distance, and status
      ```json
      {
          "id": "order_id",
          "distance": "distance_in_meters",
          "status": "UNASSIGNED"
      }
      ```
  - `400 Bad Request` if the request body is invalid
      ```json
      {
          "error": "error_message"
      }
      ```
  - `500 Internal Server Error` if an unexpected error occurs
     ```json
     {
          "error": "error_message"
     }
     ```


### List Orders

- **URL:** `/orders`
- **Method:** `GET`
- **Query Parameters:**
    - `page` (required, long, minimum: 1)
    - `limit` (required, long, minimum: 1)
- **Response:**
    - `200 OK` with a list of orders
    ```json
    [
        {
            "id": "order_id",
            "distance": "distance_in_meters",
            "status": "UNASSIGNED"
        }
    ]
    ```
    - `400 Bad Request` if the query parameters are invalid
    ```json
    {
        "error": "error_message"
    }
    ```
    - `500 Internal Server Error` if an unexpected error occurs
    ```json
    {
        "error": "error_message"
    }
    ```


### Take Order

- **URL:** `/orders/{id}`
- **Method:** `PATCH`
- **Path Parameters:**
    - `id` (required, long) - the order ID
- **Request Body:**
    ```json
    {
        "status": "TAKEN"
    }
    ```
- **Response:**
    - `200 OK` with the updated order status
      ```json
      {
          "status": "SUCCESS"
      }
      ```
    - `400 Bad Request` if the order ID is not existing or the order has been taken
      ```json
      {
          "error": "error_message"
      }
      ```
    - `500 Internal Server Error` if an unexpected error occurs
      ```json
      {
          "error": "error_message"
      }
      ```
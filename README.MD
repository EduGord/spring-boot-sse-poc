# Server-Sent Events (SSE) Spring Boot POC

This project demonstrates the implementation of Server-Sent Events (SSE) using Spring Boot for real-time updates on payment status.

## Project Structure

- `src/main/java/com/edugord/server_side_event_poc`: Contains the Java source files.
- `src/main/resources`: Contains application properties and SQL schema definitions.
- `node-client`: Contains the Node.js client-side application.

## Requirements

- Java 21 or higher
- Maven
- Node.js (for the client-side application)
- npm (Node Package Manager)

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/EduGord/spring-boot-sse-poc.git
cd ssespringbootpoc
```

### 2. Build and Run the Spring Boot Application

```bash
mvn spring-boot:run
```

### 3. Setup the Node.js Client

Navigate to the `node-client` directory within your project:

```bash
cd node-client
```

### 4. Install Dependencies

```bash
npm install
```

### 5. Run the Node.js Client

```bash
node client.js
```

## Usage

Once the Spring Boot application is running, you can use the Node.js client to simulate payment requests and subscribe to payment status updates via SSE.

### API Endpoints

- **POST `/auth/sign-in`**: Authenticates the user and returns a JWT token to be used for the authenticated routes.
- **POST `/payment/pay`**: Initiates a payment request, returns a payment ID to be subscribed to.
- **GET `/payment/status/{paymentId}`**: Subscribes to payment status updates for a specific payment.

## Configuration

### Spring Boot Application Configuration

The Spring Boot application uses H2 in-memory database. Configuration properties can be found in `src/main/resources/application.properties`.

### Node.js Client Configuration

The Node.js client connects to the SSE endpoint of the Spring Boot application. Update the URL in `client.js` if necessary.

## Notes

- This POC does not include production-grade security measures and is meant for demonstration purposes only.
- Customize and extend the functionality as per your application requirements.

# Task 1 — Java Backend and REST API

This project implements a backend service in Java using Spring Boot that manages "Task" objects and their executions. The application exposes RESTful endpoints for creating, retrieving, deleting, and executing tasks.

---

## Tech Stack used

- Java 17+

- Spring Boot

- MongoDB (docker container)

- Maven

- REST API (JSON)

- Postman

---

## Domain Model

### Task

- `id`: String — Unique task ID

- `name`: String — Task name

- `owner`: String — Task owner

- `command`: String — Shell command to execute

- `taskExecutions`: List of `TaskExecution`

### TaskExecution

- `startTime`: Date — Execution start time

- `endTime`: Date — Execution end time

- `output`: String — Command output

---

## API Endpoints

| Method | Endpoint | Description |

|--------|-----------------------------|------------------------------------------|

| GET | `/tasks` | Get all tasks |

| GET | `/tasks?id={id}` | Get task by ID |

| PUT | `/tasks` | Create or update a task |

| DELETE | `/tasks?id={id}` | Delete task by ID |

| GET | `/tasks/search?name={name}` | Search tasks by name |

| PUT | `/tasks/{id}/execute` | Execute task command and store output |

---

## Command Validation

Only safe shell commands are allowed. The following are permitted:

```text

echo, ls, pwd, whoami, date, uptime, cat, touch, mkdir, sleep, head, tail, df, du
```

## How to Run the Application

### Prerequisites

Before running the app, ensure the following are installed:

- Java 17+

- Maven

- MongoDB via Docker

---

### Step 1: Clone the Repository

```bash
git clone https://github.com/Jithin-b-p/task-java-backend

cd task-java-backend
```

### Step 2: Start mongodb in docker

```bash
docker run -d -p 27017:27017 --name task-mongo mongo
```

### Step 3: Build the project

```bash
mvn clean install
```

### Step 4: Run the application

```bash

mvn spring-boot:run
```

### Step 5: Test the API enpoints (use Postman/curl)

## screenshots

### Postman requests

![get all tasks](/srnshots/srnshot-1.png)
![create task](/srnshots/srnshot-2-putreq.png)
![get task by id](/srnshots/srnshot-3.png)
![get task by name](/srnshots/scrnshot-4.png)
![delete task](/srnshots/srnshot-5.png)

### Database content

![mongodb running](/srnshots/srnshot-6.png)
![mongodb content](/srnshots/scrnshot-7.png)

# EasyFix Service System 🛠️

EasyFix Service System is a robust, multi-role RESTful API platform designed to streamline property maintenance, service discovery, and provider portfolio tracking. The application simplifies the workflow between customers needing home repairs and certified service providers managing public portfolios, availability schedules, and client feedback.

Built using the **Spring Boot** ecosystem, the backend utilizes modern concurrency control mechanisms for scheduling management, automated database migrations, and role-based access security, completely containerized for local development and production orchestration.

---

## 📝 Project Description

EasyFix Service System is a full-stack, multi-role RESTful API platform designed to optimize home maintenance service management and marketplace discovery. The system bridges the gap between homeowners (Customers) looking for reliable repairs and technicians (Providers) managing their digital business profiles.

The application implements strict role-based authorization to secure sensitive operational workflows. Customers can browse localized service categories, book open time slots, and submit verified feedback. Providers are equipped with dedicated tools to showcase multi-image work portfolios, configure calendar lockouts, and track client reviews. Engineered with a strict focus on data integrity, the system utilizes advanced concurrency controls to prevent scheduling conflicts, ensuring seamless transaction handling during peak booking windows.

---

## 🚀 Tech Stack

* **Backend Core:** Java 17, Spring Boot 3.2.3
* **Data Access & Persistence:** Spring Data JPA, Hibernate
* **Database:** PostgreSQL 11.4
* **Database Client Panel:** pgAdmin 4
* **Security Framework:** Spring Security, JWT (JSON Web Tokens)
* **Build Tooling & Testing:** Apache Maven, JUnit 5, Mockito
* **Containerization:** Docker, Docker Compose

---
## 🔗 User Stories

[View User Stories](https://trello.com/b/JaISbkV2/easyfix)

---

## 🗄️ ERD Diagram

![ERD Diagram](<img width="1358" height="690" alt="EasyFix-ERD" src="https://github.com/user-attachments/assets/58ba3b0d-46a5-4296-821a-c2f5d3b82b5c" />
)

> ERD shows entities: Users, ServiceCategories, Appointments, Reviews, ProviderPortfolios, ProviderBusySlots.  
> Relationships reflect real-world interactions:  
> ```
> ServiceCategory 1 ──── * Users (Providers)
> User (Provider) 1 ──── * ProviderPortfolios
> User (Provider) 1 ──── * ProviderBusySlots
>
> User (Customer) 1 ──── * Appointments
> User (Provider) 1 ──── * Appointments
> 
> Appointments    1 ──── 1 Reviews
> User (Customer) 1 ──── * Reviews
> User (Provider) 1 ──── * Reviews
> ```

---

## REST API Endpoints

### 🔐 User & Authentication Endpoints (`/auth/users`)

| Request Type | URL | Functionality | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/users/register` | Register a new user account | PUBLIC |
| `GET` | `/auth/users/verify` | Verify email/account activation token | PUBLIC |
| `POST` | `/auth/users/login` | Authenticate user and return JWT token | PUBLIC |
| `POST` | `/auth/users/change-password` | Update account password while logged in | ANY Authenticated User |
| `POST` | `/auth/users/forgot-password`| Request a password reset link via email | PUBLIC |
| `GET` | `/auth/users/reset-password` | Process/validate the password reset token | PUBLIC |
| `GET` | `/auth/users/all` | Retrieve a list of all system users | `ADMIN` Only |
| `GET` | `/auth/users/pending-providers`| Fetch providers waiting for account approval | `ADMIN` Only |
| `PUT` | `/auth/users/provider/onboarding`| Complete provider business profile details | `PROVIDER` Only |
| `PUT` | `/auth/users/approve-provider/{id}`| Approve a pending service provider profile | `ADMIN` Only |
| `POST` | `/auth/users/upload-image` | Upload an image file | ANY Authenticated User |
| `GET` | `/auth/users/images/{filename}`| Retrieve/stream an uploaded image file | PUBLIC |
| `PUT` | `/auth/users/update-profile` | Update profile text data & upload avatar image | ANY Authenticated User |
| `PATCH`| `/auth/users/delete/{Id}` | Soft-delete a user account by its ID | `ADMIN` Only |

### 🖼️ Provider Portfolio Endpoints (`/api/portfolio`)

| Request Type | URL | Functionality | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/portfolio/upload` | Upload showcase photos to work portfolio | `PROVIDER` Only |
| `GET` | `/api/portfolio/provider/{providerId}` | Retrieve all public portfolio media for a provider | PUBLIC |
| `DELETE` | `/api/portfolio/delete/{imageId}` | Purge a project image from a portfolio by its ID | `ADMIN` Only |


### 🗂️ Service Category Endpoints (`/api/categories`)

| Request Type | URL | Functionality | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Retrieve all available service categories | PUBLIC |
| `POST` | `/api/categories/create` | Add a new service category (e.g., Plumbing) | `ADMIN` Only |
| `DELETE` | `/api/categories/{id}` | Permanently delete a service category by its ID | `ADMIN` Only |

### 📅 Availability Slot Endpoints (`/api/slots`)

| Request Type | URL | Functionality | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/slots/available` | Fetch a provider's open, bookable calendar slots | PUBLIC |


### 📝 Appointment Endpoints (`/api/appointments`)

| Request Type | URL | Functionality | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/appointments/book` | Book an open availability slot for a service | `CUSTOMER` Only |
| `GET` | `/api/appointments/my-bookings` | Retrieve booking history for the logged-in user | ANY Authenticated User |
| `PUT` | `/api/appointments/{id}/complete` | Mark an ongoing service contract as completed | `PROVIDER` Only |


### ⭐ Review & Feedback Endpoints (`/api/reviews`)

| Request Type | URL | Functionality | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/reviews/submit` | Submit a public text review and rating for a provider | `CUSTOMER` Only |
| `GET` | `/api/reviews/provider/{providerId}` | Retrieve all public customer reviews for a provider | PUBLIC |
| `DELETE` | `/api/reviews/{reviewId}` | Administrative override to permanently delete a review | `ADMIN` Only |

---

## 🛠️ Prerequisites & Local Setup

### 1. Requirements
Ensure you have the following software installed locally:
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* [Git](https://git-scm.com/)
* An API Client like [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/)

### 2. Launching the Environment
The entire stack—including the Spring Boot application, PostgreSQL engine, and pgAdmin administration portal—is configured to launch seamlessly with Docker Compose.

1. Clone this repository to your local machine.
2. Open a terminal panel inside the project root directory.
3. Run the following command to build the Java binary and boot the multi-container network:
   ```bash
   docker-compose up --build

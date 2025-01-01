
# 📚 Book Store

## Welcome to the backend API for Book Store!

This project provides all the essential functionality for managing an 
online bookstore: from handling products to managing orders and user operations.

***

## Domain Models

### Shopping Cart Management
* **ShoppingCart:** A dedicated cart tied to each user for managing selected books.
* **CartItem:** Represents individual items in the shopping cart, including quantities and references to books.

### Order Management
* **Order:** Handles completed purchases, storing user details and a summary of items.
* **OrderItem:** Contains details of each book purchased as part of an order.

### Books and Categories
* **Book:** Represents the products available in the store, with attributes such as title, author, price, and description.
* **Category:** Organizes books into genres or thematic groups for easier navigation and filtering.

### User and Role Management
* **User:** Core entity for customer accounts, storing authentication details, personal information, and roles.
* **Role:** Manages access control with role-based permissions (e.g., ADMIN, USER).

***
## Features 📚
### User Management
*    User registration with email and password.
*    Secure user authentication and login (JWT-based).
*    Role-based access control (Admin/User).
*    Profile management (view and update user information).
### Book Catalog
*    View a list of available books.
*    Search books by title, author, or ISBN.
*    Filter books by categories or genres.
*    Sort books by price, popularity, or publication date.
*    Detailed book pages with descriptions, reviews, and ratings.
### Shopping Cart
*    Add books to the shopping cart.
*    Update the quantity of books in the cart.
*    Remove items from the cart.
*    View the total price of items in the cart.
### Order Management
*    Checkout with a summary of items and total cost.
*    View order history and statuses.
### Book Management (Admin)
*    Add, edit, or delete books from the catalog.
*    Manage book categories and genres.
*    Upload book cover images and multimedia content.
### Search
*    Search functionality by author, category, price, and title.
### Security Features
*    Secure password storage with encryption.
*    Input validation and error handling.

***

## 🛠️ Technologies Used

### Programming Language
* **Java 17:** The version of Java used for building and running the application, offering features like enhanced pattern matching, records, and improved performance.

### Backend Framework
* **Spring Boot 3.3.4:** Main framework for building the backend API with embedded Tomcat server support.

### Database
* **MySQL:** Relational database management system for storing application data.
* **H2:** In-memory database used for testing purposes.
* **Liquibase:** Database version control and schema management tool.

### Security
* **Spring Security:** Provides security services, including authentication and authorization, for the application.
* **JWT (JSON Web Tokens):** For secure user authentication using tokens.

### ORM and Persistence
* **Spring Data JPA:** Simplifies data access and integration with the database through JPA.
* **Hibernate Validator:** Used for validating Java beans based on constraints.

### Code Quality & Static Analysis
* **Checkstyle:** Ensures code quality by enforcing a set of coding standards.
* **Lombok:** Reduces boilerplate code with annotations like @Getter, @Setter, and @ToString.
* **MapStruct:** Simplifies object mapping between Java beans.
### Testing
* **JUnit 5:** Framework for writing and running tests.
* **TestContainers:** Provides lightweight, disposable containers for integration testing.
* **Spring Security Test:** Tools for testing security aspects in Spring applications.
### Documentation
* **Springdoc OpenAPI:** Automatically generates OpenAPI documentation for RESTful APIs.
### Build & Packaging
* **Maven:** Dependency management and build tool.
* **Spring Boot Maven Plugin:** For packaging the application into an executable JAR file.
### Docker
* **Docker Compose:** Manages multi-container Docker applications, useful for local development and testing.

***

## 📂 Project Structure
src
├── main
│   ├── java
│   │   └── ruslan
│   │       └── shastkiv
│   │           └── bookstore
│   │               ├── config
│   │               ├── controller
│   │               ├── dto
│   │               ├── exception
│   │               ├── mapper
│   │               ├── model
│   │               ├── repository
│   │               ├── security
│   │               ├── service
│   │               └── validation
│   ├── resources
│   │   ├── application.yml
│   │   ├── Db.changelog
│   │   └── changes
└── test
├── java
│   └── ruslan
│       └── shastkiv
│           └── bookstore
│               ├── controller
│               ├── service
│               ├── repository
│               └── utils
├── resources
│   └── scripts
│       ├── book
│       ├── cart
│       ├── category
│       ├── order
│       └── user


***
## 🛠️ Project Setup & Launch

### 1. Clone the repository:
You can use any IDE (for example, IntelliJ IDEA) and run the following command in the terminal to clone the project
[git clone https://github.com/ShastkivRuslan/online-book-shop.git](https://github.com/ShastkivRuslan/online-book-shop.git)

### 2. Launch the app on your local device or via Docker:
Create a .env file in the root directory and fill it with your own data, using the provided example from the .env.template file.

### 3. Open Swagger UI using your browser to check functionality:

***

## 🤝 Contributing

### If you would like to contribute to this project:

* Fork the repository.
* Create a new branch for your feature or fix.
* Make your changes and commit them.
* Submit a pull request.

***
## 📬 Contact us

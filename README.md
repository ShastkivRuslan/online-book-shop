# Online Book Store
This project is a Spring Boot web application for an online bookstore, which will be implemented step by step. The application will allow users to browse books, add them to a shopping cart, and place orders.

### Project Description

In this app, we will have the following domain models:

* User: Contains information about registered users, including authentication details and personal information.
* Role: Represents the role of a user within the system, such as admin or customer.
* Book: Represents a book available for purchase in the store.
* Category: Defines a category for books, allowing them to be grouped and filtered by genre or type.
* ShoppingCart: Represents a user's shopping cart, where they can temporarily store items before purchasing.
* CartItem: Represents an individual item within a user's shopping cart.
* Order: Represents a finalized purchase order made by a user.
* OrderItem: Represents individual items within an order, detailing quantities and prices.

Each part of the application will be implemented incrementally, covering all core functionalities in a layered architecture. This project serves as a practical demonstration of using Spring Boot for building a web-based application, with a focus on best practices and modular development.

By the end of this project, the repository will serve as a showcase of skills and can be shared as part of a portfolio.
src
├── main
│   ├── java/ruslan/shastkiv/bookstore
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── exception
│   │   ├── mapper
│   │   ├── model
│   │   ├── repository
│   │   ├── security
│   │   ├── service
│   │   └── validation
│   ├── resources
│   │   ├── application.yml
│   │   └── Db.changelog
│   │   └── changes
└── test
    ├── java/ruslan/shastkiv/bookstore 
    │   ├── controller 
    │   ├── service 
    │   ├── repository 
    │   └── utils 
    ├── resources 
    │   └── scripts 
    │       ├── book 
    │       ├── cart 
    │       ├── category 
    │       ├── order
    │       └── user

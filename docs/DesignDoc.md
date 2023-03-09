# PROJECT Design Documentation

## Team Information

- ### Team name: Team 4 (d) - PowerRangers
- ### Team members
  - Poorna Chander Reddy, Puttaparthi (pp5109)
  - Andrews, Rajasekar (ar1516)
  - Ragu Madhavan Loganathan (rl6824)
  - Jagrat, Rao (jr6714)
  - Haotian, Zhang (hz3127)

## Executive Summary

- EduMart - is an Estore which sells digital material and books related to wide variety of topics. It allows users browse the catelog, view descriptions, add items to cart and checkout (on authentication). It also houses some inventory management features for the control of admin. Like any estore it helps retain and browse the inventory and purchase history.

- Edumart also packs some newly introduced feature enhancements where authors of products registered with the platform can now request admin to add new products to the inventory followed by an admin feature to approve author requests and add item to the e-store.

- Edumart is built upon Angular and Spring boot frameworks with Typescript and Java as predominant languaguages used.

### Purpose

E-Store (Edumart) is developed to broadly
achieve the following using an E-Store to accelerate an existing small business:

- Users must easily browse through books and videos related to topics of interest
- Users must view details of products including author details
- Users must be able to add items ot cart and checkout
- Users must have a purchase history
- Admins must be able to manage the inventory (add remove check products)
- Authors must be able to request new product addition
- Admin must be able to approve author requests for product addition

### Glossary and Acronyms

| Term    | Definition                                                                          |
| ------- | ----------------------------------------------------------------------------------- |
| CRUD    | Create, Read, Update and Delete                                                     |
| E-store | a web based online shopping system, in this document it also refers to this project |

## Requirements

This section describes the features of the application.

### Definition of MVP

Minimum Viable Product: is a product with sufficient features to validate in the early phases of the development. they not only complete the most critical features but also help acquire a go ahead for next phases of project.

### MVP Features

#### Authentication:

- User Login:

  - As a User, I want to login to the E-Store, Using username and password, to buy learning Product online.

- User Login as guest:

  - As a User, I want to access the E-store as a guest, to only browse and not checkout.

- Admin Login:

  - As an Admin, I want to login to E-store with admin username and admin password, to manage inventory and author requests.

#### Customer Functionality:

- View all products:

  - As a User, I must see all products of E-Store listed after I login, to browse and search.

- Filter/Search E-Store for the right product (Epic):

  - Customers can use features to refine their search for products.

  - Search with text:

    - As a User, I would want to search by typing in topic of study, to list down related product.related products.

  - Select by product category:

    - As a User, I would want to choose between one or all types of product (Video, Audio,Book) to refine the list of products displayed.

- Manage Cart (Epic):

  - As a user I want to manage my shopping cart

  - Adding Products to cart

    - As a User, I want to add products to my shopping cart, to update the items in cart.

  - Removing products from cart

    - As a User, I want to remove products from my shopping cart, to update the items in cart.

  - Check out

    - As a User, I want to check-out the current items in my cart, to make payment.

  - Payment

    As a User, I want to choose between debit or credit card for payment, to pay and complete
    shopping.

- Inventory Management (Epic):

  - Stock addition:

    - As an Admin, I would want to view and update current stock of products, to make sure inventory aligns with what customer sees.

  - Remove from Inventory:

    - As an Admin, I want to remove products that are no longer procured from inventory register, to make sure they are displayed as "permanently unavailable" to the user.

  - Add new product to Inventory:

    - As an Admin, I want to add new products to the inventory, so that they will be automatically
      listed on the E-Store.

#### Data Persistence:

- Retain User information:

  - As a User, I want to retain user information even if I logout.

- View previously added items in cart:

  - As a User, I want to view my cart next time I login, to check out later.

- Purchase History:

  - As a User, I want to view my purchase history, to buy same items again.

- Inventory History:

  - As an Admin, I want to view past inventory changes, to analyze any tally issues.

### Roadmap of Enhancements

#### 10% Feature, Enhancement 1:

- Add Authors to the E-Store and allow limited control to enhance their products and information to increase the user satisfaction and content credibility.

- Author Authentication:

  - As an Author, I would want to login using admin provided username and password, to request for new product addition.

- Author specific Functionality (Epic):

  - Request for Revise/Update Products:

    - As an author, I want to raise request to revise/update my content (videos/books/audios) on the E-store, so that admin can review and approve it.

  - Added Functionality for Admin:

    - As an admin, I want to review and approve product addition/update requests from Authors, to update their listings on E-Store.

#### 10% Feature, Enhancement 2:

- Users can purchase virtual one-on-one interaction sessions from select authors.\_

- Specific Virtual Session purchase:

  - As a User, if author supports one-on-one sessions, I would want to view and add a one-to-one
    session to cart, for attending a one-to-one virtual session with author.

- Consistency in purchase experience for products and sessions:

  - As a User, I want to add one to one session to cart like any other product, to purchase and view in history.

## Application Domain

This section describes the application domain.

![Domain Model](DomainModel_TeamD.jpg)

#### Important Domain entities and their segregation:

- User: Customer, Admin, Author

- Product: Video, Book

- Customer Service: History, Cart, Payment

- E-store Service: Inventory

#### High level Description of major domain model entities and relations:

• _Customer, Admin, Author are specializations of user_

• _Video, Book are specializations of product_

• _Product Catelog is a collection of products_

• _History class manages both purchase and inventory history and is invoked by customer, payment, admin, and inventory classes_

• _state of cart is changed by customer class operations_

## Architecture and Design

This section describes the application architecture.

### Summary

The following Tiers/Layers model shows a high-level view of the webapp's architecture.

![The Tiers & Layers of the Architecture](architecture-tiers-and-layers.png)

The e-store web application, is built using the Model–View–ViewModel (MVVM) architecture pattern.

The Model stores the application data objects including any functionality to provide persistance.

The View is the client-side SPA built with Angular utilizing HTML, CSS and TypeScript. The ViewModel provides RESTful APIs to the client (View) as well as any logic required to manipulate the data objects from the Model.

Both the ViewModel and Model are built using Java and Spring Framework. Details of the components within these tiers are supplied below.

### Overview of User Interface

This section describes the web interface flow; this is how the user views and interacts
with the e-store application.

#### Login page:

![](UIScreenshotsPart2/login.png)

#### Home page:

![](UIScreenshotsPart2/home.png)

![](UIScreenshotsPart2/home2.png)

#### Cart Page:

![](UIScreenshotsPart2/cart.png)

#### Admin Main Page - delete add update items:

![](UIScreenshotsPart2/admin_add_items.png)

#### Admin Page - add new product:

![](UIScreenshotsPart2/admin_add_product.png)

#### Admin Page - update product:

![](UIScreenshotsPart2/admin_update_product.png)

#### Author Product Request:

![](UIScreenshotsPart2/author_request.png)

#### Search:

![](UIScreenshotsPart2/product_search.png)

#### Purchase History:

![](UIScreenshotsPart2/purchase_history.png)

#### Inventory History:

![](UIScreenshotsPart2/admin_inventory_history.png)

### View Tier

summary of the View Tier UI with respect to the achitecture and related angular components:

##### major components:

- app.home - angular component related to the landing home page (all products and filters displayed)

- app.login - angular compnent that deals with user input for authentication and registration

- app.Product-single-view - angular component that contains detailed product description and add button

- app.cart - angular component with all views and buttons related to cart and checkout

- app.admin-inv-all-list - angular component of page where admin eadds or removes items from inventory

- app.purchase-history - purchase history of customer

##### major serveces:

- app.product.service.ts -- responsible for communication with the spring backedn using REST api for all product related queries

- app.session.service.ts -- responsible for communications related to sessions booked.

- app.cart.service.spec.ts -- responcible for all REST requests related to cart management based on user interaction through the UI

##### important usecases and sequence/flow of operations

- ###### 1) customer adding items to cart:

![](SequenceDiagrams/User_Add_items_to_cart.jpg)

- ###### 2) Admin adding new product in inventory:

![](SequenceDiagrams/Admin_Create_Product.jpg)

- ###### 3) User Authentication:

![](SequenceDiagrams/authentacate_user.png)

- ###### 4) Admin Update Product:

![](SequenceDiagrams/update_product.png)

- ###### 5) User Search Product:

![](SequenceDiagrams/search_product.jpg)

### ViewModel Tier

This tier acts more like a control layer that bridges the User interaction to the business logic
It houses the REST backend interfacea nd routing an dnavigation logics

The major class that can be classified here is StoreController (as seen in the class diagram below):

as an example : it houses the function getAllProducts which accepts a GET HTTP request from the front end to return the entire product list.
app.product.service.ts hosts a client method which is invoked when customer enters the landing page. this method inturn communicates to StoreController.getAllProducts() to fetch all related products.

Similarly various methods in StoreController class serve different REST endpoints (ex: PUT, POST etc) to make sure the request from the angular service is interpreted and routed to the right backend business function and the result of the backed is sent back as a response to the angular.

This module leverages RESTful capabilities of Spring framework.

#### Class and Package Diagrams

#### Package Diagram:

- The system is divided into three Packages (as shown below):
  ![](ClassDiagrams/packageDiagram.png)

#### Static view of classes and relatons within packages:

![](ClassDiagrams/classsrelation.png)

#### Detailed class diagram of Model Package:

![](ClassDiagrams/modelclassdiag.png)

#### Detailed class diagram of persestince Package:

![](ClassDiagrams/persistenceclassdiag.png)

#### Detailed class diagram of controller Package:

![](ClassDiagrams/controllerclassdiag.png)

### Model Tier

#### Classes holding the attribute structure and schema (refer to diagram above):

- Prouct and its specialisations Video and Books
- Cart
- UserDetails and its specializations Author, User and Admin

#### DAO interfaces:

- CartDao
- HistoyDao
- UserDao
- ProductDao

#### DAO implementations:

- ProductFileDAO (implements Product Dao -- > all major product related business logic)
- CartFileDAO
- HistoryFileDAO
- UserFileDAO

### Static Code Analysis and Design Improvements

##### Recommended Design Improvements:

- Split Controller into multiple modules to separate the concerns and increase maintainability.

- Add Utils package to centralize some business logic and reduce the redundancy of logic in similar functions or pages.

- Add service classes where necessary to separate concerns and responsibilities of the DAO. So that DAO is only focusing on data access.

- Add another routing layer on top of the api controllers based on category of request.

- The ProductFileDAO must be broken in to sub classes to handle the complexity. currently it Genralises all the Access operations related to products. it can be split based on the user specific usecases.

- Sessions can be saperated out from products cart since they are zero priced items and hence can have a saperate methodology to handle them. also session details and authors ability to edit dates can be enhanced.

- Post improvemtns in code standards an dpractices. the following images show the status of Static code analysis and the picture of quality:

##### Static code analysis:

- Key changes made during sprint 3 to reduce the code smell and other concerns shown up during sprint 2

  - String repetency avoiding – CodeSmellFixer java class was introduced to define and reuse names and strings. This helped maintain standard and avoiding case changes of same variables.

  - String readability was improved using standard camel case style guides

  - Exception handling was made more specific:

    - Ex: throw new Exception("INVALID_BODY") was changed to throw new IllegalArgumentException();

  - Variable naming convention standards were practiced better.

  - Declaration of data structures was changed to match standards of latest java.
    - Ex: ArrayList<Integer> a = new ArrayList<Integer>() was changed to ArrayList<Integer> a = new ArrayList<>()

- Sonar cube and sonar lint were the tools used to check the codesmell and the latest results are as follows:

###### Backend API change of metrics

![](StaticCodeAnalysis/BackendAPImetricsImprovements.png)

###### Frontend UI

![](StaticCodeAnalysis/FrontendUImetricsImprovements.png)

## Testing

This section will provide information about the testing performed

### Acceptance Testing

- Number of user storeis with acceptance tests passed: sprint 3: 9 , sprint 2: 8, sprint 1: 6
- Number of User Stories with acceptance tests failed: sprint 3: 0, sprint 2: 1, sprint 1: 0

### Unit Testing and Code Coverage

Unit test were written for all key routing and business logic handling functions, the test cases were designed to cover most critical paths of the code flow to increase the code coverage. mocking behaviour was used where appropriate. the coverage of the unit testing done sofar are as follows:

#### Controller Tier - coverage

The following is the coverage at the Controller Tier code for the project

![](CodeCoverageScreenShots/controller_tier_high_level.png)

![](CodeCoverageScreenShots/controller_low_level.png)

#### Model Tier - coverage

The following is the coverage at the Model Tier code for the project.

![](CodeCoverageScreenShots/model_tier_high_level.png)

#### Persistence Tier - coverage

The following is the coverage at the Persistence Tier code for the project.

![](CodeCoverageScreenShots/persistance_high_level.png)

#### Change in code coverage from sprint 2 to sprint 3:

- Retesting refactored modules and exponential growth in new functionality forced the team to focus only on crucial parts of testing, in the time available for sprint 3. Hence, thought he coverage has increases the percentage coverage shows up lesser than sprint 2.

![](CodeCoverageScreenShots/coveragechangesprint3.png)

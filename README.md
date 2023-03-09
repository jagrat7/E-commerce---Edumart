# E-Store: **\_** EduMart - Estore That sells Books and other Learning Content**\_**

An online E-store system built in Java 8=>11 and springboot 2.6.2

## Team

- Poorna Chander Reddy (pp5109@rit.edu)
- Jagrat Rao
- Andrews Rajasekar(ar1516@g.rit.edu)
- Haotian Zhang
- Ragu Madhavan(rl6826@g.rit.edu)

## Prerequisites

- Java 8=>11 (Make sure to have correct JAVA_HOME setup in your environment)
- Maven
- Spring

## How to run it

1. Clone the repository and go to the root directory.
2. Execute `mvn compile exec:java`
3. Open in your browser `http://localhost:8080/`
4. _add any other steps required or examples of how to use/run_

## Known bugs and disclaimers

(It may be the case that your implementation is not perfect.)

Document any known bug or nuisance.
If any shortcomings, make clear what these are and where they are located.

## How to test it

The Maven build script provides hooks for run unit tests and generate code coverage
reports in HTML.

To run tests on all tiers together do this:

1. Execute `mvn clean test jacoco:report`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/index.html`

To run tests on a single tier do this:

1. Execute `mvn clean test-compile surefire:test@tier jacoco:report@tier` where `tier` is one of `controller`, `model`, `persistence`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/{controller, model, persistence}/index.html`

To run tests on all the tiers in isolation do this:

1. Execute `mvn exec:exec@tests-and-coverage`
2. To view the Controller tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
3. To view the Model tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
4. To view the Persistence tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`

\*(Consider using `mvn clean verify` to attest you have reached the target threshold for coverage)
## Curl command examples
1. get single product: curl -X GET http://localhost:8080/inventory/product/v1 | json
2. get all products: curl -X GET http://localhost:8080/inventory/products | json
3. search a product: curl -X GET http://localhost:8080/inventory/search?name=yoga for all&type=book | json  
4. delete product: curl -X DELETE http://localhost:8080/inventory/b2 | json
5. create product: curl -L -X POST "http://localhost:8080/inventory/productrequest" -H "Content-Type: application/json" --data-raw "{ \"name\": \"yoga for poorna\", \"price\": 5.25, \"rating\": 4.0, \"topic\": \"fitness\", \"description\": \".....\", \"author\": \"James\", \"quantity\": 7, \"pages\": 45, \"weight\": 0.3, \"duration\": 360, \"size\": 65.0, \"type\": \"book\" }" | json
6. update product: curl -X PUT -H "Content-Type: application/json" "http://localhost:8080/inventory/update" -d "{\"id\":\"v5\",\"price\": 24.3,\"quantity\":11}" | json

## How to generate the Design documentation PDF

1. Access the `PROJECT_DOCS_HOME/` directory
2. Execute `mvn exec:exec@docs`
3. The generated PDF will be in `PROJECT_DOCS_HOME/` directory

## How to setup/run/test program

1. Tester, first obtain the Acceptance Test plan
2. IP address of target machine running the app
3. Execute **\_\_\_\_**
4. ...
5. ...

## License

MIT License

See LICENSE for details.
"# E-commerce---Edumart" 

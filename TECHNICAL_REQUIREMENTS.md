## WebSocket Random Numbers Server

---

### Task description

Write a Java server that acts as a websocket server. Upon a request to the server, a response should be returned in the form of JSON - a random BigInteger. 

---

### Requirements

#### Functional requirements

- The server should not allow more than 1 connection to be established from one IP address.
- The server must ensure that the number will always be unique, having appeared once - it should not appear again.

#### Non-functional requirements

- Do not use frameworks like Spring, RxJava, etc.
- Describe in the readme how to launch and use the server.

Unit-tests and docker are not mandatory, if there are comments in the code.

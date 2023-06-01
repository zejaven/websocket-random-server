## WebSocket Random Number Server launch instructions

---

### Build & Run with Docker & Docker Compose

1. To launch the application, Docker and Docker Compose must be installed on the system and corresponding system environment variables must be set.
2. Use **start-docker.bat** to launch the application on Windows or **start-docker.sh** to launch it on Linux.
3. Once the application is deployed, WebSocket endpoint can be accessed via Postman using the following url: ***ws://localhost:8080/websocket_random_server/random***.

> The environment where the application was launched successfully:
> 
> Windows:
> - Docker (20.10.11, build dea9396)
> - Docker Compose (1.29.2, build 5becea4c)
> 
> Linux:
> - Docker (23.0.1, build a5ee5b1)
> - Docker Compose (v2.16.0)

---

### Alternative Build & Run with Java & Tomcat

1. To launch the application, Java 17, Tomcat and IntelliJ IDEA must be installed on the system.
2. The project should be loaded in IntelliJ IDEA as Maven project.
3. Run **mvn clean install**.
4. Open Run/Debug Configurations and add new Local Tomcat server **(IMPORTANT: not less than 10th version!)**
5. In Deployment tab add artifact **websocket-random-server:war exploded**
6. Apply changes and run the application.
7. Once the application is deployed, WebSocket endpoint can be accessed via Postman using the following url: ***ws://localhost:8080/websocket_random_server/random***.

> The environment where the application was launched successfully:
>
> Windows:
> - Java (17.0.3.1 2022-04-22 LTS)
> - Tomcat (10.1.9)

---

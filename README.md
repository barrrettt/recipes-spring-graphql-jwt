# recipes-spring-graphql-jwt
GraphQL API with springboot and JWT. This is my GraphQl java base proyect with my own security logic.
I rely on the repositories and examples from the official graphql pages,thx!

The business logic is author-recipe-tag, With 3 user roles: ADMIN, SUPER, USER. Access permissions are controlled in the datafetcher functions.

## Install
I used mysql but there are no native queries. Easy install with docker-compose:
```console
sudo apt install docker-compose
git clone ... etc
sudo docker-compose up --build
```
In other case you can modify the properties and launch with gradle.
```console
./gradlew bootRun
```
## Use
Api can be consumed by default on port 9000. you can use a graphql client such as Altair, Firecamp or Postman, among others. Can get JWT with ADMIN user:
```graphql
mutation {
  signIn(name: "admin" password: "admin")
}
```
![image](https://user-images.githubusercontent.com/47840319/148543602-8e8cbc9a-5e7c-41d3-8ac2-6c94cec954dc.png)

You must use the token in each request, putting it in the Authorization header, example:
```
Authorization : Bearer xxxx.yyyy.xxxx
```
![image](https://user-images.githubusercontent.com/47840319/148544441-f3ecac31-c37c-425f-ac39-20272b6a5e87.png)

![image](https://user-images.githubusercontent.com/47840319/148544568-3284180c-9674-42ac-9c1a-bc49f11d0f65.png)

## References
Spring Boot https://github.com/spring-projects/spring-boot

GraphQL Java https://www.graphql-java.com/

JWT https://github.com/jwtk/jjwt


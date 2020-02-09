# BioThenticate Java API
This is a java implementation of the BioThenticate API. It is made to make developers lives easier when integrating with BioThenticate. 

This API offers simplistic methods/functions to interact with BioThenticate. 

## Usage 

### Maven & Gradle
--- TODO add maven dep code ---  
--- TODO add gradle dep code ---

### Quick Start
Below will print out all of the users in your BioThenticate tenant
```java
public static void main(String[] args) {
    BioThenticateClient client = new BioThenticateClient("email@example.com", "secret_password");
    User[] users = client.getAllUsers(UsersFilter.NONE, true);
    
    System.out.println(Arrays.toString(users));
}
```

### Login
```java
LoginResponse response = this.client.login("email@example.com", "secret_password");
if (!response.isSuccess()) {
    String token = response.getToken();
    TokenUser user = this.client.parseToken(token);
}
```

### Authenticate (MFA)
```java
AuthenticateResponse response = this.client.authenticate(AuthenticationType.IRIS, "email@example.com", "Someone is requesting to sign in", "", "My Application");
if (!response.isAuthenticated()) {
    if (response.isTimedOut()) {
        // Request timed out
    }
    // Request was denied
}
// Request was successful
```

## Build
For the most part you will not need to build this from the source as you can get it from gradle, maven or elsewhere. 
But it is supported if needed.

1. Clone the repository ---TODO insert url here---
2. Navigate to the root directory and run `gradlew clean build`
3. Once completed the .jar will be located at `build/libs/`

## License
--- TODO insert license details here ----

## Authors
The [SOFTwarfare](https://softwarfare.com) Dev Team

# Kotlin DSL for Wiremock
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.marcinziolo/kotlin-wiremock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.marcinziolo/kotlin-wiremock)
[![Build Status](https://travis-ci.org/marcinziolo/kotlin-wiremock.svg?branch=master)](https://travis-ci.org/marcinziolo/kotlin-wiremock)
[![codecov](https://codecov.io/gh/marcinziolo/kotlin-wiremock/branch/master/graph/badge.svg)](https://codecov.io/gh/marcinziolo/kotlin-wiremock)

This is library provides handy kotlin dsl for [Wiremock](http://wiremock.org/) stubbing.

### Getting started

Gradle
```kotlin
  testImplementation("com.marcinziolo:kotlin-wiremock:version")
```

Maven

```xml
<dependency>
    <groupId>com.marcinziolo</groupId>
    <artifactId>kotlin-wiremock</artifactId>
    <version>:version</version>
    <scope>test</scope>
</dependency>
```
### Features

* Request Matching
    * Http methods (post/get/put/delete/patch/options/head/trace/any)
        ```kotlin
        wiremock.post {
            url equalTo "/users/1"
        } returnsJson {
          body = """
          {
            "id": 1,
            "name": "Bob"
          }
          """
        }
        ```       
    * JSON body - with strong type checking of json value
        ```kotlin
        wiremock.post {
            body contains "id" equalTo 1
            body contains "name" like "Alice"
            body contains "isAdmin" equalTo true
            body contains "points" equalTo 3.0
            body contains "lastName" // just checking if key exists
        }
        ```
    * Headers
        ```kotlin
        wiremock.post {
            headers contains "User-Agent" like "Firefox.*" 
        }
        ``` 
    * Query parameters
        ```kotlin
        wiremock.post {
            queryParams contains "page" like "1.*" 
        }  
        ```  
    * Priority
        ```kotlin
        wiremock.post {
            url equalTo "/test"
            priority = 2
        } returnsJson  {
            statusCode = 403
        }

        wiremock.post {
            url equalTo "/test"
            headers contains "Authorization"
            priority = 1
        } returnsJson  {
            statusCode = 200
        }
        ```
    * Cookies        
* Response specification
    * Returns
        ```kotlin
        wiremock.get {
            url equalTo "/users/1"
        } returns {
            statusCode = 200
            header = "Content-Type" to "application/json"
            body = """
            {
              "id": 1,
              "name": "Bob"
            }
            """
        }
        ```
    * Or easier `returnsJson`
        ```kotlin
        wiremock.get {
            url equalTo "/users/1"
        } returnsJson {
            body = """
            {
              "id": 1,
              "name": "Bob"
            }
            """
        }
        ```
    * Delays
      ```kotlin
      wiremock.post {
          url equalTo "/users"
      } returnsJson {
          delay fixedMs 100
          //or gaussian distribution
          delay medianMs 100 sigma 0.1
      }       
      ``` 
* Chaining
     ```kotlin
      val bobResponse: SpecifyResponse = {
          body = """
              {
                "id": 1,
                "name": "Bob"
              }
              """
      }
  
      wiremock.post {
          url equalTo "/users"
      } and {
          body contains "id" equalTo 1
      } and {
          body contains "isAdmin" equalTo true
      } returns {
          header = "Content-Type" to "application/json"
      } and bobResponse and {
          statusCode = 201
      }
     ```
* Scenarios - stateful behaviour
   ```kotlin
    wiremock.post {
        url equalTo "/users"
    } returnsJson bobResponse and {
        toState = "Alice"
    }

    wiremock.post {
        url equalTo "/users"
        whenState = "Alice"
    } returnsJson aliceResponse and {
        clearState = true
    }
   ```
* WiremockTest Junit5 extension
    ```kotlin
    @WireMockTest
    class ExampleExtensionTest {
    
        lateinit var url: String
    
        @BeforeEach
        fun urlSetup(wmRuntimeInfo: WireMockRuntimeInfo) {
            url = wmRuntimeInfo.httpBaseUrl
        }
    
        @Test
        fun `url equalTo`() {
            mockGet {
                url equalTo "/users/1"
            } returns {
                header = "Content-Type" to "application/json"
                statusCode = 200
                body = """
                {
                  "id": 1,
                  "name": "Bob"
                }
                """
            }
    
            When {
                get("$url/users/1")
            } Then {
                statusCode(200)
                body("id", equalTo(1))
                body("name", equalTo("Bob"))
            }
        }
    }
    ```
#### More examples

* [Examples.kt](src/test/kotlin/com/marcinziolo/kotlin/wiremock/ExampleTest.kt)
* [JUnit4 example](kotlin-wiremock-examples/src/test/kotlin/com/marcinziolo/kotlin/wiremock/JUnit4ExampleTest.kt)
* [Junit5 base class](src/test/kotlin/com/marcinziolo/kotlin/wiremock/AbstractTest.kt)

### Compatibility

The Library is compatible with Wiremock - 2.8.0 and higher
 
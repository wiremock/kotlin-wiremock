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
            urlPath equalTo "/users/1"
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
* Verification api
   ```kotlin
    wm.verify {
       headers contains "User-Agent" equalTo "curl"
       queryParams contains "filter" equalTo "true"
       cookies contains "cookieKey" equalTo "cookieValue"
       body contains "pet" equalTo "dog"
   }
  
   //exactly
   wm.verify {
     urlPath equalTo "/users/1"
     exactly = 1
   } moreThan 2 //lessThan lessThanOrEqual  moreThanOrEqual
  
   //atLeast atMost
   wm.verify {
     urlPath equalTo "/users/1"
     atLeast = 1
     atMost = 3
   }
   ```  

* WiremockTest Junit5 extension
    ```kotlin
    class Junit5RegisterExtensionTest {
    
        @JvmField
        @RegisterExtension
        var wm = WireMockExtension.newInstance()
                .options(wireMockConfig().dynamicPort())
                .build()
    
        @Test
        fun testGet() {
            wm.get {
                url equalTo "/hello"
            } returns {
                statusCode = 200
            }
    
            When {
                get("${wm.baseUrl()}/hello")
            } Then {
                statusCode(200)
            }
        }
    }
    ```
#### More examples

* [Examples.kt](src/test/kotlin/com/marcinziolo/kotlin/wiremock/ExampleTest.kt)
* [JUnit4 example](kotlin-wiremock-examples/src/test/kotlin/com/marcinziolo/kotlin/wiremock/JUnit4ExampleTest.kt)
* [Junit5 base class](src/test/kotlin/com/marcinziolo/kotlin/wiremock/AbstractTest.kt)
* [Verification test cases](src/test/kotlin/com/marcinziolo/kotlin/wiremock/Verify.kt)
* [WithBuilderTests](src/test/kotlin/com/marcinziolo/kotlin/wiremock/otherPackage/WithBuilderTest.kt) - extension point for using original wiremock api and its extensions(like webhooks)

### Compatibility

The Library is compatible with Wiremock - 2.8.0 and higher

### Release notes

| Version | Notes                                                                                                                                                                                                                                                                                                                                      |
|:-------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  2.0.2  | Supports wiremock 3.0.0 and Jetty 11                                                                                                                                                                                                                                                                                                       |
|  2.0.0  | Breaking change for verification DSL - changed api for specifying counting                                                                                                                                                                                                                                                                 |
|  1.1.0  | Introduced DSL for [verfication API](https://wiremock.org/docs/verifying/)                                                                                                                                                                                                                                                                 |
|  1.0.5  | In version 1.0.4 `url` argument (eg.`url equalTo "/hello"`) was treated as a path and matches only a path of url, which was wrong and misleading, in version 1.0.5 it was fixed and new keyword `urlPath` was introduced for matching a path of url (eg.`urlPath equalTo "/hello"`). Note: `url` has precedence in case both are specified |
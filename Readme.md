# Build Application
* Clone or download the project
* Go to the root folder of the project
* Use this command to build and run using docker
  ```
      ./mvn clean package
      docker build -t my-pos .
      docker run --rm --name my-pos my-pos:latest
  ```
* Use this command to build and run using maven
  ```
      ./mvn clean package
      java -jar ./target/demo-0.0.1-SNAPSHOT.jar
  ```
## Point System
You manage a POS integrated e-commerce platform which offers a point system and has a vast collection of payment methods integrated. Some payment methods require a commission fee from the payment provider thus you do not want to provide too much discount on a product if the customer selects that payment method. At the same time, you would want to control the points given per purchase based on the payment methods to minimize loss. The following is the list of possible payment methods and their rates.

Shops and online stores will integrate with your system. To make a payment, the following requests and response should be provided

```
Request:
{
    "price": "100.00",
    "price_modifier": 0.95,
    "payment_method": "MASTERCARD",
    "datetime": "2022-09-01T00:00:00Z"
}
```
```
Response
{
    "final_price": "95.00",
    "points": 5 
}
```

### Calculation

final_price = 100 * 0.95 = 95
points = 100 * 0.05 = 5

If the input is invalid, your system should respond in the following format.
{

	"error": {
		"errorCode": <Error Code>,
		"errorMessage": <Error Message>
	}
}

## Sales List
You should also allow the users of your e-commerce system to see how much sales were made within a date range broken down into hours. Your system should be able to show a list of sales and the points given out to the customer.
```
Request
{
    "startDateTime": "2022-09-01T00:00:00Z",
    "endDateTime": "2022-09-01T23:59:59Z"
} 
```
```
Response
{
  "sales": [
    {
      "datetime": "2022-09-01T00:00:00Z",
      "sales": "1000.00",
      "points": 10
    },
    {
      "datetime": "2022-09-01T01:00:00Z",
      "sales": "2000.00",
      "points": 20
    },
    {
      "datetime": "2022-09-02T00:00:00Z",
      "sales": "5000.00",
      "points": 75
    },
    ...
    {
      "datetime": "2022-09-01T23:00:00Z",
      "sales": "7000.00",
      "points": 30
    }
  ]
}
```
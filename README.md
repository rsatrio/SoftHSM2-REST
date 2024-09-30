# SoftHSM2 REST API

A simple REST API for SoftHSM2. This project is build with dropwizard 2.x,Java 8, SoftHSM2, and Docker.

## Features

- REST Endpoint
- Swagger page to test the API
- Create RSA Key on Initialized Slot at SoftHSM2
- Signing Base64 data with Key stored in SoftHSM2. Signing Mechanism is using SHA256withRSA
- Retrieve RSA Public Key from HSM
- Dockerized


## Build

- Run docker build
```shell
docker build -t softhsm-rest .
```
- Run the docker image from previous step:
```shell
docker run -p 9080:9080 softhsm-rest
```
- Take note of the Slot ID created and User PIN (check Dockerfile, it should be 1234567)

- Go to the swagger page (http://localhost:9080/swagger), and test creating key in the SlotID (or just enter 0) created before. Use the /v1/softhsm/create-key endpoint
- If key successfully created, then try to sign Base64 data with that key. For convenience, you can use the swagger interface earlier. Use the /v1/softhsm/sign-data endpoint

## Feedback

For feedback, please raise issues in the issue section of the repository. Periodically, I will update the example with more real-life use case example. Enjoy!!.


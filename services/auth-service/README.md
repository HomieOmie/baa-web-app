### HTTP STATUS CODES
| Request       | Info                                                                                | Status Code |
|---------------|-------------------------------------------------------------------------------------|-------------|
| OPTIONS       | Used by API Gateway to check with Lambda function                                   | 200         |
| signup        | All Header and Body content are validated and user is signed up successfully        | 201         |
| signup        | Missing Header or body in the request                                               | 400         |
| signup        | Authentication header indicates that user is not signed in as an Admin user type    | 401         |
| signup        | Necessary Header content is missing                                                 | 400         |
| signup        | Necessary body content is missing                                                   | 400         |
| signup        | Request was valid but user was not created by AWS Cognito for some reason           | 500         |
| signup        | Request was valid but request is taking far too long                                | 504         |
| confirmSignup | All Header and Body content are validated and user signup is confirmed              | 204         |
| confirmSignup | All Header and Body content are validated but user signup confirmation is not valid | 400         |
| confirmSignup | Missing Header or body in the request                                               | 400         |
| confirmSignup | Necessary Header content is missing                                                 | 400         |
| confirmSignup | Necessary body content is missing                                                   | 400         |
| confirmSignup | Request was valid but user was not confirmed by AWS Cognito for some reason         | 500         |
| confirmSignup | Request was valid but request is taking far too long                                | 504         |
| login         | All Header and Body content are validated and user login is confirmed               | 200         |
| login         | All Header and Body content are validated but user login confirmation is not valid  | 400         |
| login         | Missing Header or body in the request                                               | 400         |
| login         | Necessary Header content is missing                                                 | 400         |
| login         | Necessary body content is missing                                                   | 400         |
| login         | Request was valid but user was not confirmed by AWS Cognito for some reason         | 500         |
| login         | Request was valid but request is taking far too long                                | 504         |
| listUsers     | All Header and Body content are validated and users are returned successfully       | 201         |
| listUsers     | Missing Header or body in the request                                               | 400         |
| listUsers     | Authentication header indicates that user is not signed in as an Admin user type    | 401         |
| listUsers     | Necessary Header content is missing                                                 | 400         |
| listUsers     | Necessary body content is missing                                                   | 400         |
| listUsers     | Request was valid but users were not found by AWS Cognito for some reason           | 500         |
| listUsers     | Request was valid but request is taking far too long                                | 504         |


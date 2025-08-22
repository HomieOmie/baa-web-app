import * as cdk from 'aws-cdk-lib';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as path from 'path';
import { Construct } from 'constructs';

export class CognitoAuthStack extends cdk.Stack {
  public readonly userPool: cognito.UserPool;
  public readonly userPoolClient: cognito.UserPoolClient;
  public readonly authLambda: lambda.Function;
  public readonly api: apigateway.RestApi;

  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
      super(scope, id, {
          ...props,
          env: {
              account: process.env.CDK_DEFAULT_ACCOUNT,
              region: process.env.CDK_DEFAULT_REGION,
          },
      });

    this.userPool = new cognito.UserPool(this, 'UserPool', {
      userPoolName: 'pusher-user-pool',
      selfSignUpEnabled: true,
      signInAliases: {
          email: true,
          username: true
      },
      autoVerify: { email: true },
      passwordPolicy: {
        minLength: 8,
        requireLowercase: true,
        requireUppercase: true,
        requireDigits: true,
      },
    });

    this.userPoolClient = new cognito.UserPoolClient(this, 'UserPoolClient', {
      userPool: this.userPool,
      generateSecret: false,
      authFlows: {
        userPassword: true,
        userSrp: true,
      },
    });

    new cdk.CfnOutput(this, 'UserPoolId', { value: this.userPool.userPoolId });
    new cdk.CfnOutput(this, 'ClientId', { value: this.userPoolClient.userPoolClientId });

    // --- Lambda Function ---
    this.authLambda = new lambda.Function(this, 'AuthServiceLambda', {
      runtime: lambda.Runtime.JAVA_21, // or JAVA_17 if using older runtime
      handler: 'com.homieomie.authservice.AuthServiceMain::handleRequest',
      memorySize: 1024,
      timeout: cdk.Duration.seconds(15),
      code: lambda.Code.fromAsset(path.join(__dirname, '../../services/auth-service/target/auth-service-1.0-SNAPSHOT.jar')),
      environment: {
          USER_POOL_ID: this.userPool.userPoolId,
          CLIENT_ID: this.userPoolClient.userPoolClientId,
      },
    });

    // --- Grant Lambda permissions to call Cognito ---
    this.authLambda.addToRolePolicy(new iam.PolicyStatement({
      actions: [
          "cognito-idp:SignUp",
          "cognito-idp:ConfirmSignUp",
          "cognito-idp:AdminInitiateAuth",
          "cognito-idp:AdminGetUser",
          "cognito-idp:AdminCreateUser",
          "cognito-idp:AdminConfirmSignUp",
          "cognito-idp:AdminSetUserPassword"
      ],
      resources: [this.userPool.userPoolArn],
    }));

      // --- API Gateway REST API ---
      this.api = new apigateway.RestApi(this, 'AuthApi', {
          restApiName: 'AuthServiceApi',
          description: 'API for authentication (signup, login, verify)',
          deployOptions: {
              stageName: 'prod',
          },
      });

      // --- /signup resource ---
      const signupResource = this.api.root.addResource('signup');

      signupResource.addMethod(
          'POST',
          new apigateway.LambdaIntegration(this.authLambda, { proxy: true }),
          {
              authorizationType: apigateway.AuthorizationType.NONE,
          }
      );

      // Add CORS preflight
      signupResource.addCorsPreflight({
          allowOrigins: apigateway.Cors.ALL_ORIGINS,
          allowMethods: ['POST', 'OPTIONS'],
          allowHeaders: ['Content-Type', 'Authorization'],
      });

      // --- /confirmSignup resource ---
      const confirmSignupResource = this.api.root.addResource('confirmSignup');

      confirmSignupResource.addMethod(
          'POST',
          new apigateway.LambdaIntegration(this.authLambda, { proxy: true }),
          { authorizationType: apigateway.AuthorizationType.NONE }
      );

      // Enable CORS
      confirmSignupResource.addCorsPreflight({
          allowOrigins: apigateway.Cors.ALL_ORIGINS,
          allowMethods: ['POST', 'OPTIONS'],
          allowHeaders: ['Content-Type', 'Authorization'],
      });

      new cdk.CfnOutput(this, 'ApiEndpoint', { value: this.api.url });

  }
}

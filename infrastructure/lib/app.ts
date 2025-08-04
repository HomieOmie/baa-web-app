import * as cdk from 'aws-cdk-lib';
import { CognitoAuthStack } from './cognito-auth-stack';

const app = new cdk.App();
new CognitoAuthStack(app, 'CognitoAuthStack');

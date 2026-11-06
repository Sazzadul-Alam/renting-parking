import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../environments/environment';

export const authCodeFlowConfig: AuthConfig = {
  issuer: environment.issuer,
  redirectUri: window.location.origin + '/',
  postLogoutRedirectUri: window.location.origin + '/',
  clientId: environment.clientId,
  responseType: 'code',
  scope: 'openid profile api.read api.write',
  showDebugInformation: true,
  timeoutFactor: 0.75,
  clearHashAfterLogin: true
};


import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authCodeFlowConfig } from '../auth.config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private oauthService: OAuthService) {}

  public initAuth(): Promise<boolean> {
    this.oauthService.configure(authCodeFlowConfig);
    return this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  public login(): void {
    this.oauthService.initCodeFlow();
  }

  public logout(): void {
    this.oauthService.logOut();
  }

  public isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  public getAccessToken(): string {
    return this.oauthService.getAccessToken();
  }

  public getIdentityClaims(): any {
    return this.oauthService.getIdentityClaims();
  }

  public getUsername(): string {
    const claims = this.getIdentityClaims();
    if (!claims) return '';
    return claims.username || claims.sub || claims.preferred_username || '';
  }

  public getRoles(): string[] {
    const claims = this.getIdentityClaims();
    if (!claims || !claims.roles) return [];
    return Array.isArray(claims.roles) ? claims.roles : [claims.roles];
  }

  public hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }
}

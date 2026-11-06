import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  template: `
    <div class="card">
      <h2>Welcome to the Connected Microservices Platform</h2>
      <p class="subtitle">Built with Spring Boot 3.3.5, Spring Cloud 2023.0.3, MongoDB Atlas, RabbitMQ, and Angular 15</p>

      <div *ngIf="isLoggedIn()" class="status-box authenticated">
        <p><strong>Authenticated as:</strong> {{ getUsername() }}</p>
        <p><strong>Roles:</strong> {{ getRoles().join(', ') }}</p>
        <div class="actions">
          <button class="btn btn-secondary" (click)="logout()">Log Out</button>
        </div>
      </div>

      <div *ngIf="!isLoggedIn()" class="status-box unauthenticated">
        <p>You are currently not logged in.</p>
        <div class="actions">
          <button class="btn btn-primary" (click)="login()">Log In via OAuth2 (PKCE)</button>
          <a routerLink="/register" class="btn btn-outline">Register New Account</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .subtitle { color: #666; margin-bottom: 2rem; }
    .status-box { padding: 1.5rem; border-radius: 8px; margin-top: 1rem; }
    .authenticated { background: #e8f5e9; border: 1px solid #c8e6c9; }
    .unauthenticated { background: #f5f5f5; border: 1px solid #e0e0e0; }
    .actions { margin-top: 1.25rem; display: flex; gap: 1rem; align-items: center; }
  `]
})
export class HomeComponent implements OnInit {
  constructor(private authService: AuthService) {}

  ngOnInit(): void {}

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUsername(): string {
    return this.authService.getUsername();
  }

  getRoles(): string[] {
    return this.authService.getRoles();
  }

  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }
}

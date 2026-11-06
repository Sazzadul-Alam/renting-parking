import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  template: `
    <div class="layout">
      <header class="navbar">
        <div class="nav-brand">
          <a routerLink="/"><strong>Platform</strong> Microservices</a>
        </div>
        <nav class="nav-links">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Home</a>
          <ng-container *ngIf="isLoggedIn()">
            <a routerLink="/items" routerLinkActive="active">Items</a>
            <a routerLink="/events" routerLinkActive="active">Events</a>
            <a routerLink="/profile" routerLinkActive="active">Profile</a>
          </ng-container>
          <a *ngIf="!isLoggedIn()" routerLink="/register" routerLinkActive="active">Register</a>
        </nav>
        <div class="nav-auth">
          <ng-container *ngIf="isLoggedIn()">
            <span class="user-greeting">Hi, <strong>{{ getUsername() }}</strong></span>
            <button class="btn btn-outline-light btn-sm" (click)="logout()">Logout</button>
          </ng-container>
          <ng-container *ngIf="!isLoggedIn()">
            <button class="btn btn-light btn-sm" (click)="login()">Login</button>
          </ng-container>
        </div>
      </header>

      <main class="main-content">
        <router-outlet></router-outlet>
      </main>

      <footer class="footer">
        <p>&copy; 2026 Microservices Platform (Java 21 + Spring Boot 3.3.5 + Angular 15)</p>
      </footer>
    </div>
  `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private authService: AuthService) {}

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUsername(): string {
    return this.authService.getUsername();
  }

  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }
}

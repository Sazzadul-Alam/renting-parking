import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  template: `
    <div class="card form-card">
      <h2>Register New Account</h2>
      <p class="subtitle">Creates a user document in MongoDB Atlas & fires a RabbitMQ <code>user.registered</code> event</p>

      <div *ngIf="errorMessage" class="alert error">{{ errorMessage }}</div>
      <div *ngIf="successMessage" class="alert success">{{ successMessage }}</div>

      <form (ngSubmit)="onSubmit()" #regForm="ngForm">
        <div class="form-group">
          <label>Username</label>
          <input type="text" [(ngModel)]="model.username" name="username" required minlength="3" class="input" />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input type="email" [(ngModel)]="model.email" name="email" required email class="input" />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input type="password" [(ngModel)]="model.password" name="password" required minlength="6" class="input" />
        </div>

        <div class="actions">
          <button type="submit" class="btn btn-primary" [disabled]="!regForm.form.valid || loading">
            {{ loading ? 'Registering...' : 'Register' }}
          </button>
          <a routerLink="/" class="btn btn-outline">Cancel</a>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .form-card { max-width: 480px; margin: 0 auto; }
    .subtitle { color: #666; margin-bottom: 1.5rem; font-size: 0.9rem; }
    .subtitle code { background: #e2e8f0; padding: 0.1rem 0.3rem; border-radius: 4px; }
    .form-group { margin-bottom: 1.25rem; }
    .form-group label { display: block; font-weight: 600; margin-bottom: 0.5rem; font-size: 0.9rem; }
    .input { width: 100%; box-sizing: border-box; padding: 0.6rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.95rem; }
    .actions { display: flex; gap: 1rem; margin-top: 1.5rem; }
    .alert { padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; }
    .error { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; }
    .success { background: #dcfce7; border: 1px solid #86efac; color: #166534; }
  `]
})
export class RegisterComponent {
  model = { username: '', email: '', password: '' };
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.apiService.register(this.model).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = `User '${res.username}' registered successfully! Redirecting to login...`;
        setTimeout(() => {
          this.authService.login();
        }, 1200);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Registration failed';
      }
    });
  }
}


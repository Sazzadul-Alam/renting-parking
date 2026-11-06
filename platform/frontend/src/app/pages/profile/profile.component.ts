import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  template: `
    <div class="card">
      <h2>User Profile</h2>
      <p class="subtitle">ID Token claims issued by OAuth2 Authorization Server</p>

      <div class="profile-details" *ngIf="claims">
        <div class="detail-row">
          <span class="label">Username:</span>
          <span class="val">{{ getUsername() }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Email:</span>
          <span class="val">{{ claims.email || 'N/A' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Roles:</span>
          <span class="val badge" *ngFor="let role of getRoles()">{{ role }}</span>
        </div>
        <div class="detail-row">
          <span class="label">Subject (sub):</span>
          <span class="val">{{ claims.sub }}</span>
        </div>
      </div>

      <div class="raw-claims">
        <h3>All Raw Claims</h3>
        <pre>{{ claims | json }}</pre>
      </div>
    </div>
  `,
  styles: [`
    .subtitle { color: #666; margin-bottom: 1.5rem; }
    .profile-details { display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 2rem; }
    .detail-row { display: flex; gap: 1rem; align-items: center; }
    .label { font-weight: 600; width: 140px; }
    .badge { background: #e3f2fd; color: #1976d2; padding: 0.2rem 0.6rem; border-radius: 4px; font-size: 0.85rem; font-weight: 500; margin-right: 0.5rem; }
    .raw-claims pre { background: #f8f9fa; padding: 1rem; border-radius: 6px; overflow-x: auto; font-size: 0.85rem; }
  `]
})
export class ProfileComponent implements OnInit {
  claims: any = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.claims = this.authService.getIdentityClaims();
  }

  getUsername(): string {
    return this.authService.getUsername();
  }

  getRoles(): string[] {
    return this.authService.getRoles();
  }
}

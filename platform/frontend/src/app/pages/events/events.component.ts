import { Component, OnInit } from '@angular/core';
import { ApiService, UserRegisteredEvent } from '../../services/api.service';

@Component({
  selector: 'app-events',
  template: `
    <div class="card">
      <div class="header-row">
        <div>
          <h2>RabbitMQ Registration Events</h2>
          <p class="subtitle">Broadcasted on <code>platform.events</code> with routing key <code>user.registered</code>, consumed by Web Service</p>
        </div>
        <button class="btn btn-outline" (click)="loadEvents()">Refresh</button>
      </div>

      <div *ngIf="errorMessage" class="alert error">{{ errorMessage }}</div>

      <table class="table" *ngIf="events.length > 0">
        <thead>
          <tr>
            <th>User ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Created At</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let ev of events">
            <td><code>{{ ev.userId }}</code></td>
            <td><strong>{{ ev.username }}</strong></td>
            <td>{{ ev.email }}</td>
            <td>{{ ev.createdAt | date:'medium' }}</td>
          </tr>
        </tbody>
      </table>

      <div *ngIf="events.length === 0 && !loading" class="empty-state">
        No registration events received yet. Register a new user to test the RabbitMQ event stream!
      </div>
    </div>
  `,
  styles: [`
    .header-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .subtitle { color: #666; margin-top: 0.25rem; }
    .subtitle code { background: #e2e8f0; padding: 0.1rem 0.3rem; border-radius: 4px; }
    .table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
    .table th, .table td { padding: 0.75rem 1rem; border-bottom: 1px solid #e2e8f0; text-align: left; }
    .table th { background: #f8fafc; font-weight: 600; color: #475569; }
    .alert { padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; }
    .error { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; }
    .empty-state { text-align: center; padding: 2.5rem; color: #64748b; background: #f8fafc; border-radius: 8px; }
  `]
})
export class EventsComponent implements OnInit {
  events: UserRegisteredEvent[] = [];
  loading = false;
  errorMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading = true;
    this.errorMessage = '';
    this.apiService.getUserEvents().subscribe({
      next: (res) => {
        this.events = res;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load user events: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }
}


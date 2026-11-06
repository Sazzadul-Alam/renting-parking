import { Component, OnInit } from '@angular/core';
import { ApiService, Item } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-items',
  template: `
    <div class="card">
      <div class="header-row">
        <div>
          <h2>Items Management</h2>
          <p class="subtitle">Protected resource fetched via API Gateway (Port 8080) -> Web Service (Port 8081)</p>
        </div>
        <button class="btn btn-outline" (click)="loadItems()">Refresh</button>
      </div>

      <div *ngIf="errorMessage" class="alert error">{{ errorMessage }}</div>
      <div *ngIf="successMessage" class="alert success">{{ successMessage }}</div>

      <!-- Add item form (ROLE_USER) -->
      <div class="form-container">
        <h3>Add New Item</h3>
        <form (ngSubmit)="addItem()" #itemForm="ngForm">
          <div class="form-row">
            <input type="text" [(ngModel)]="newItem.name" name="name" placeholder="Item Name" required class="input" />
            <input type="text" [(ngModel)]="newItem.description" name="description" placeholder="Description" required class="input" />
            <button type="submit" class="btn btn-primary" [disabled]="!itemForm.form.valid || loading">Add Item</button>
          </div>
        </form>
      </div>

      <!-- Items List -->
      <table class="table" *ngIf="items.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Created By</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let item of items">
            <td><code>{{ item.id }}</code></td>
            <td><strong>{{ item.name }}</strong></td>
            <td>{{ item.description }}</td>
            <td><span class="badge">{{ item.createdBy }}</span></td>
            <td>
              <button *ngIf="isAdmin()" class="btn btn-danger btn-sm" (click)="deleteItem(item.id!)" [disabled]="loading">
                Delete (Admin)
              </button>
              <span *ngIf="!isAdmin()" class="text-muted">Requires ROLE_ADMIN</span>
            </td>
          </tr>
        </tbody>
      </table>

      <div *ngIf="items.length === 0 && !loading" class="empty-state">
        No items found. Add one above!
      </div>
    </div>
  `,
  styles: [`
    .header-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
    .subtitle { color: #666; margin-top: 0.25rem; }
    .form-container { background: #fdfdfd; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.25rem; margin-bottom: 2rem; }
    .form-container h3 { margin-bottom: 0.75rem; font-size: 1.1rem; }
    .form-row { display: flex; gap: 0.75rem; flex-wrap: wrap; }
    .input { flex: 1; min-width: 200px; padding: 0.5rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.95rem; }
    .table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
    .table th, .table td { padding: 0.75rem 1rem; border-bottom: 1px solid #e2e8f0; text-align: left; }
    .table th { background: #f8fafc; font-weight: 600; color: #475569; }
    .badge { background: #f1f5f9; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.85rem; }
    .btn-sm { padding: 0.25rem 0.6rem; font-size: 0.85rem; }
    .btn-danger { background: #ef4444; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .btn-danger:hover { background: #dc2626; }
    .text-muted { color: #94a3b8; font-size: 0.85rem; font-style: italic; }
    .alert { padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; }
    .error { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b; }
    .success { background: #dcfce7; border: 1px solid #86efac; color: #166534; }
    .empty-state { text-align: center; padding: 2rem; color: #64748b; }
  `]
})
export class ItemsComponent implements OnInit {
  items: Item[] = [];
  newItem: Item = { name: '', description: '' };
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadItems();
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ROLE_ADMIN');
  }

  loadItems(): void {
    this.loading = true;
    this.errorMessage = '';
    this.apiService.getItems().subscribe({
      next: (res) => {
        this.items = res;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load items: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  addItem(): void {
    if (!this.newItem.name || !this.newItem.description) return;
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.apiService.addItem(this.newItem).subscribe({
      next: (res) => {
        this.items.push(res);
        this.newItem = { name: '', description: '' };
        this.successMessage = 'Item added successfully!';
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to add item: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  deleteItem(id: string): void {
    if (!confirm('Are you sure you want to delete this item?')) return;
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.apiService.deleteItem(id).subscribe({
      next: () => {
        this.items = this.items.filter(item => item.id !== id);
        this.successMessage = 'Item deleted successfully!';
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to delete item: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }
}


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Item {
  id?: string;
  name: string;
  description: string;
  createdBy?: string;
}

export interface UserRegisteredEvent {
  userId: string;
  username: string;
  email: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private gatewayUrl = environment.gatewayUrl;

  constructor(private http: HttpClient) {}

  register(data: any): Observable<any> {
    return this.http.post(`${this.gatewayUrl}/api/auth/register`, data);
  }

  getItems(): Observable<Item[]> {
    return this.http.get<Item[]>(`${this.gatewayUrl}/api/web/items`);
  }

  addItem(item: Item): Observable<Item> {
    return this.http.post<Item>(`${this.gatewayUrl}/api/web/items`, item);
  }

  deleteItem(id: string): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/api/web/items/${id}`);
  }

  getUserEvents(): Observable<UserRegisteredEvent[]> {
    return this.http.get<UserRegisteredEvent[]>(`${this.gatewayUrl}/api/web/events/users`);
  }
}


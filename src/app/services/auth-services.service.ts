import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class AuthServicesService {

  private accessTokenKey = 'accessToken';
  private decodedToken: any = null;

  constructor(private route:Router) { }

  saveToken(token: string): void {
    localStorage.setItem(this.accessTokenKey, token);
    this.decodeToken(token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.accessTokenKey);
  }

  private decodeToken(token: string): void {
    try {
      this.decodedToken = jwtDecode(token);
    } catch (error) {
      console.error('Invalid token:', error);
    }
  }

  getDecodedToken(): any {
    if (!this.decodedToken) {
      const token = this.getToken();
      if (token) {
        this.decodeToken(token);
      }
    }
    return this.decodedToken;
  }

  getUserId(): string | null {
    const decoded = this.getDecodedToken();
    return decoded?.sub || null;
  }

  getName() : string | null {
    const decode = this.getDecodedToken();
    return decode?.name || null;
  }

  getUsername(): string | null {
    const decoded = this.getDecodedToken();
    return decoded?.preferred_username || decoded?.username || null;
  }

  getRoles(): string[] {
    const decoded = this.getDecodedToken();
    const roles = decoded?.realm_access?.roles || [];
    return roles.filter((role: string) => role !== 'default-roles-dev' && role !== 'offline_access');
  }
  
  hasRole(role: string): boolean {
    const roles = this.getRoles();
    return roles.includes(role);
  }

  logout(): void {
    localStorage.removeItem(this.accessTokenKey);
    this.decodedToken = null;
    this.route.navigate(['']);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    const decoded = this.getDecodedToken();
    const exp = decoded?.exp; // Expiration time in seconds since the epoch
    const now = Math.floor(Date.now() / 1000); // Current time in seconds since the epoch

    if (exp && exp < now) {
      // Token has expired, logout the user
      this.logout();
      return false;
    }

    return true; 
  }
}

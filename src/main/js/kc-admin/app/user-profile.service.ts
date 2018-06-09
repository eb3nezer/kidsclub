import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';
import { User } from "./model/user";
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  private meUrl = 'https://localhost:8444/rest/users/me';

  constructor(private http: HttpClient) {

  }

  getUserObservable(): Observable<User> {
    return this.http.get<User>(this.meUrl);
  }
}

import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';
import { User } from "../model/user";
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
    private meUrlForGet = '/rest/users/me';
    private meUrlForUpdate = '/rest/users';

  constructor(private http: HttpClient) {

  }

  getCurrentUserObservable(): Observable<User> {
    return this.http.get<User>(this.meUrlForGet);
  }

  // updateCurrentUser(updated: User): void {
  //     return this.http.put(this.meUrlForUpdate, updated);
  // }
}

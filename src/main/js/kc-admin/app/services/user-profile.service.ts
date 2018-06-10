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
    private urlForUserSearch = '/rest/users';

  constructor(private http: HttpClient) {

  }

  getCurrentUserObservable(): Observable<User> {
    return this.http.get<User>(this.meUrlForGet);
  }

    updateCurrentUserObservable(user: User, profilePhoto: File): Observable<User> {
        const formData: FormData = new FormData();
        for (var property in user) {
            if (user.hasOwnProperty(property)) {
                formData.append(property, user[property]);
            }
        }
        if (profilePhoto) {
            formData.append("file", profilePhoto, profilePhoto.name);
        }
        return this.http.post(this.meUrlForUpdate, formData);
    }

    loadUserMatchingForProject(projectId: number, query: string): Observable<User[]> {
        return this.http.get<User[]>(`${this.urlForUserSearch}?projectId=${projectId}&name=${query}`);
    }
}

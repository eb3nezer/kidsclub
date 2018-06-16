import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {catchError} from "rxjs/operators";
import {UserInvitation} from "../model/userInvitation";
import {User} from "../model/user";
import {BulkUserInvitation} from "../model/bulkUserInvitation";

@Injectable({
  providedIn: 'root'
})
export class InviteService {
    private url = '/rest/invite';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    inviteUser(projectId: number, email: string, name: string): Observable<User> {
        const invitation = new UserInvitation(email, name, projectId);
        return this.http.put<User>(this.url, invitation).pipe(
            catchError(this.errorService.handleError('Invite user', undefined))
        );
    }

    unInviteUser(projectId: number, userId: number): Observable<any> {
        return this.http.delete(`${this.url}/${userId}/project/${projectId}`).pipe(
            catchError(this.errorService.handleError('Uninvite user', undefined))
        );
    }

    inviteUsers(projectId: number, emails: string): Observable<User[]> {
        const invitation = new BulkUserInvitation(emails, projectId);
        return this.http.put<User[]>(`${this.url}/bulk`, invitation).pipe(
            catchError(this.errorService.handleError('Invite users', undefined))
        );
    }
}

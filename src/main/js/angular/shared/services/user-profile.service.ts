import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from "../model/user";
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {catchError} from "rxjs/operators";
import {PermissionRecord} from "../model/permissionRecord";
import {UserPermissions} from "../model/userPermissions";
import {Project} from "../model/project";

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
    private meUrlForGet = '/rest/users/me';
    private urlForGetUpdate = '/rest/users';
    private urlForUserSearch = '/rest/users';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    getCurrentUserObservable(): Observable<User> {
        return this.http.get<User>(this.meUrlForGet).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get current user', undefined))
        );
    }

    getCurrentUserById(userId: number): Observable<User> {
        return this.http.get<User>(`${this.urlForGetUpdate}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get user', undefined))
        );
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
        return this.http.post(this.urlForGetUpdate, formData).pipe(
            catchError(this.errorService.handleErrorWithDialog('Update current user', undefined))
        );
    }

    loadUserMatchingForProject(projectId: number, query: string): Observable<User[]> {
        return this.http.get<User[]>(`${this.urlForUserSearch}?projectId=${projectId}&name=${query}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Find users', []))
        );
    }

    getPermissionsForUserAndProject(projectId: number, userId: number): Observable<UserPermissions> {
        return this.http.get<UserPermissions>(`${this.urlForGetUpdate}/${userId}/permissions?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get permissions', undefined))
        );
    }

    getMyPermissionsForProject(projectId: number): Observable<UserPermissions> {
        return this.http.get<UserPermissions>(`${this.urlForGetUpdate}/me/permissions?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get permissions', undefined))
        );
    }

    getMyPermissions(): Observable<UserPermissions> {
        return this.http.get<UserPermissions>(`${this.urlForGetUpdate}/me/permissions`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get permissions', undefined))
        );
    }

    static checkProjectPermissionGranted(userPermissions: UserPermissions, permissionKey: string): boolean {
        let result = false;

        for (let userProjectPermission of userPermissions.userProjectPermissions) {
            if (userProjectPermission.key === permissionKey) {
                result = userProjectPermission.granted;
                break;
            }
        }
        return result;
    }

    static checkSitePermissionGranted(userPermissions: UserPermissions, permissionKey: string): boolean {
        let result = false;

        for (let userSitePermission of userPermissions.userSitePermissions) {
            if (userSitePermission.key === permissionKey) {
                result = userSitePermission.granted;
                break;
            }
        }
        return result;
    }

    setUserSitePermission(userId: number, permissionKey: string, newValue: boolean): Observable<UserPermissions> {
        const user = new User(userId);
        const permission = new PermissionRecord(permissionKey, "", newValue);
        const updated = new UserPermissions(user, undefined, [permission], []);
        return this.http.put<UserPermissions>(`${this.urlForGetUpdate}/${userId}/permissions`, updated).pipe(
            catchError(this.errorService.handleErrorWithDialog('Update site permission', undefined))
        );
    }

    setUserProjectPermission(userId: number, projectId: number, permissionKey: string, newValue: boolean): Observable<UserPermissions> {
        const user = new User(userId);
        const permission = new PermissionRecord(permissionKey, "", newValue);
        const project = new Project(projectId);
        const updated = new UserPermissions(user, project, [], [permission]);
        return this.http.put<UserPermissions>(`${this.urlForGetUpdate}/${userId}/permissions`, updated).pipe(
            catchError(this.errorService.handleErrorWithDialog('Update project permission', undefined))
        );
    }
}

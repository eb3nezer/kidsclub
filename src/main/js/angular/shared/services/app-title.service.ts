import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';
import {Project} from '../model/project';
import {UserPermissions} from '../model/userPermissions';

@Injectable({
  providedIn: 'root'
})
export class AppTitleService {
  titleSubscriber: Subscriber<string>;
  titleObserver: Observable<string>;
  currentProjectSubscriber: Subscriber<Project>;
  currentProjectObserver: Observable<Project>;
  userPermissionsSubscriber: Subscriber<UserPermissions>;
  userPermissionsObserver: Observable<UserPermissions>;

  constructor() {
    this.titleObserver = Observable.create(observer => this.titleSubscriber = observer);
    this.currentProjectObserver = Observable.create(observer => this.currentProjectSubscriber = observer);
    this.userPermissionsObserver = Observable.create(observer => this.userPermissionsSubscriber = observer);
  }

    getTitleObserver(): Observable<string> {
        return this.titleObserver;
    }

    setTitle(newTitle: string): void {
        this.titleSubscriber.next(newTitle);
    }

    getCurrentProjectObserver(): Observable<Project> {
      return this.currentProjectObserver;
    }

    setCurrentProject(project: Project): void {
        this.currentProjectSubscriber.next(project);
    }

    getUserPermissionsObserver(): Observable<UserPermissions> {
      return this.userPermissionsObserver;
    }

    setUserPermissions(permissions: UserPermissions): void {
      this.userPermissionsSubscriber.next(permissions);
    }
}

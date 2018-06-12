import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';
import {Project} from "../model/project";

@Injectable({
  providedIn: 'root'
})
export class AppTitleService {
  titleSubscriber: Subscriber<string>;
  titleObserver: Observable<string>;
  currentProjectSubscriber: Subscriber<Project>;
  currentProjectObserver: Observable<Project>;

  constructor() {
    this.titleObserver = Observable.create(observer => this.titleSubscriber = observer);
    this.currentProjectObserver = Observable.create(observer => this.currentProjectSubscriber = observer);
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
}

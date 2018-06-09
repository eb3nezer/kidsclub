import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApptitleService {
  titleSubscriber: Subscriber<string>;
  titleObserver: Observable<string>;

  constructor() {
    this.titleObserver = Observable.create(observer => this.titleSubscriber = observer);
  }

  getTitle(): Observable<string> {
    return this.titleObserver;
  }

  setTitle(newTitle: string): void {
    this.titleSubscriber.next(newTitle);
  }
}

import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppTitleService {
  titleSubscriber: Subscriber<string>;
  titleObserver: Observable<string>;
  messagesSubscriber: Subscriber<string>;
  messagesObserver: Observable<string>;

  constructor() {
    this.titleObserver = Observable.create(observer => this.titleSubscriber = observer);
    this.messagesObserver = Observable.create(observer => this.messagesSubscriber = observer);
  }

    getTitle(): Observable<string> {
        return this.titleObserver;
    }

    getMessages(): Observable<string> {
        return this.messagesObserver;
    }

    setTitle(newTitle: string): void {
        this.titleSubscriber.next(newTitle);
    }

    setMessages(newMessages: string): void {
        this.messagesSubscriber.next(newMessages);
    }
}

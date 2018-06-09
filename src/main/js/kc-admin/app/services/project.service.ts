import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';
import { Project } from "../model/project";
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
    private meUrlForGet = '/rest/projects';

    constructor(private http: HttpClient) {
    }

    getProjectObservable(id: number): Observable<Project> {
        return this.http.get<Project>(`${this.meUrlForGet}/${id}`);
    }

    getAllProjectsObservable(): Observable<Project[]> {
        return this.http.get<Project[]>(this.meUrlForGet);
    }
}

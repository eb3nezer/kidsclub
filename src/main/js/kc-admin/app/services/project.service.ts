import { Injectable } from '@angular/core';
import { Observable, Subscriber, of } from 'rxjs';
import { Project } from "../model/project";
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
    private urlForGet = '/rest/projects';
    private urlForUpdate = '/rest/projects';

    constructor(private http: HttpClient) {
    }

    getProjectObservable(id: number): Observable<Project> {
        return this.http.get<Project>(`${this.urlForGet}/${id}`);
    }

    updateProject(projectId: number, project: Project): Observable<Project> {
        return this.http.put<Project>(`${this.urlForUpdate}/${projectId}`, project);
    }

    getAllProjectsObservable(): Observable<Project[]> {
        return this.http.get<Project[]>(this.urlForGet);
    }
}

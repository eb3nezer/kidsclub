import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Project } from "../model/project";
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
    private urlForGet = '/rest/projects';
    private urlForUpdate = '/rest/projects';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    getProject(id: number): Observable<Project> {
        return this.http.get<Project>(`${this.urlForGet}/${id}`).pipe(
            catchError(this.errorService.handleError('Get project', undefined))
        );
    }

    updateProject(projectId: number, project: Project): Observable<Project> {
        return this.http.put<Project>(`${this.urlForUpdate}/${projectId}`, project).pipe(
            catchError(this.errorService.handleError('Update project', project))
        );
    }

    getAllProjects(): Observable<Project[]> {
        return this.http.get<Project[]>(this.urlForGet).pipe(
            catchError(this.errorService.handleError('Get all projects', []))
        );
    }

    createProject(name: string): Observable<Project> {
        const newProject = new Project(undefined, name);

        return this.http.put<Project>(this.urlForUpdate, newProject).pipe(
            catchError(this.errorService.handleError('Create project', undefined))
        );
    }
}
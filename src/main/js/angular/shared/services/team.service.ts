import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { StudentTeam } from "../model/studentTeam";
import {Project} from "../model/project";
import {HttpErrorService} from "./http-error.service";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class TeamService {
    private meUrlForGet = '/rest/students/teams';
    private meUrlForUpdate = '/rest/students/teams';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    getTeamObservable(id: number): Observable<StudentTeam> {
        return this.http.get<StudentTeam>(`${this.meUrlForGet}/${id}`).pipe(
            catchError(this.errorService.handleError('Get team', undefined))
        );
    }

    getMyTeams(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.meUrlForGet}/?projectId=${projectId}&mine=true`).pipe(
            catchError(this.errorService.handleError('Get my teams', undefined))
        );
    }

    getTeamsForProject(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.meUrlForGet}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleError('Get teams', []))
        );
    }

    updateTeamObservable(team: StudentTeam, studentList: string, leaderList: string, photo?: File, ): Observable<StudentTeam> {
        const formData: FormData = new FormData();

        const properties = ["id", "name", "projectId", "mediaDescriptor", "avatarUrl"];
        properties.forEach(propname => {
            if (team.hasOwnProperty(propname)) {
                formData.append(propname, team[propname]);
            }
        });
        formData.append("studentList", studentList);
        formData.append("leaderList", leaderList);
        formData.append("projectId", team.project.id.toString());

        if (photo) {
            formData.append("file", photo, photo.name);
        }
        return this.http.post(`${this.meUrlForUpdate}/${team.id}`, formData).pipe(
            catchError(this.errorService.handleError('Update teams', team))
        );
    }

    createTeam(team: StudentTeam, projectId: number, photo?: File): Observable<StudentTeam> {
        const formData: FormData = new FormData();
        const project = new Project(projectId);
        team.project = project;

        formData.append("projectId", projectId.toString());
        formData.append("name", team.name);

        if (photo) {
            formData.append("file", photo, photo.name);
        }
        return this.http.post(this.meUrlForUpdate, formData).pipe(
            catchError(this.errorService.handleError('Create team', undefined))
        );
    }
}

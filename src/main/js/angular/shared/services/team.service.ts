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
    private teamsUrl = '/rest/students/teams';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    getTeam(id: number): Observable<StudentTeam> {
        return this.http.get<StudentTeam>(`${this.teamsUrl}/${id}`).pipe(
            catchError(this.errorService.handleError('Get team', undefined))
        );
    }

    getMyTeams(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.teamsUrl}/?projectId=${projectId}&mine=true`).pipe(
            catchError(this.errorService.handleError('Get my teams', undefined))
        );
    }

    getTeamsForProject(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.teamsUrl}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleError('Get teams', []))
        );
    }

    updateTeam(team: StudentTeam, studentList: string, leaderList: string, photo?: File, ): Observable<StudentTeam> {
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
        return this.http.post(`${this.teamsUrl}/${team.id}`, formData).pipe(
            catchError(this.errorService.handleError('Update teams', team))
        );
    }

    updatePoints(teamId: number, points: number): Observable<StudentTeam> {
        return this.http.put(`${this.teamsUrl}/${teamId}/points/${points}`, undefined).pipe(
            catchError(this.errorService.handleError('Update team points', undefined))
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
        return this.http.post(this.teamsUrl, formData).pipe(
            catchError(this.errorService.handleError('Create team', undefined))
        );
    }
}

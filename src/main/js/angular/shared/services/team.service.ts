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
            catchError(this.errorService.handleErrorWithDialog('Get team', undefined))
        );
    }

    // If there is an error, don't display it, but just return undefined
    getTeamLogError(id: number): Observable<StudentTeam> {
        return this.http.get<StudentTeam>(`${this.teamsUrl}/${id}`).pipe(
            catchError(this.errorService.handleErrorWithConsoleLog('Get team', undefined))
        );
    }

    getMyTeams(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.teamsUrl}/?projectId=${projectId}&mine=true`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get my teams', undefined))
        );
    }

    getTeamsForProject(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.teamsUrl}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get teams', []))
        );
    }

    // If there is an error, don't display it, but just return undefined
    getTeamsForProjectLogError(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.teamsUrl}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithConsoleLog('Get teams', undefined))
        );
    }

    updateTeam(team: StudentTeam, studentList: string, leaderList: string, photo?: File, ): Observable<StudentTeam> {
        const formData: FormData = new FormData();

        const properties = ["id", "name", "projectId", "mediaDescriptor"];
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
            catchError(this.errorService.handleErrorWithDialog('Update teams', team))
        );
    }

    updatePoints(teamId: number, points: number): Observable<StudentTeam> {
        return this.http.put(`${this.teamsUrl}/${teamId}/points/${points}`, undefined).pipe(
            catchError(this.errorService.handleErrorWithDialog('Update team points', undefined))
        );
    }

    resetPoints(projectId: number): Observable<StudentTeam[]> {
        return this.http.put(`${this.teamsUrl}/points/reset?projectId=${projectId}`, undefined).pipe(
            catchError(this.errorService.handleErrorWithDialog('Reset team points', undefined))
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
            catchError(this.errorService.handleErrorWithDialog('Create team', undefined))
        );
    }
}

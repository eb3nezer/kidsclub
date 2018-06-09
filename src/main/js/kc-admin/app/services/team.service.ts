import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { StudentTeam } from "../model/studentTeam";

@Injectable({
  providedIn: 'root'
})
export class TeamService {
    private meUrlForGet = '/rest/students/teams';

    constructor(private http: HttpClient) {
    }

    getTeamObservable(id: number): Observable<StudentTeam> {
        return this.http.get<StudentTeam>(`${this.meUrlForGet}/${id}`);
    }

    getTeamsForProject(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.meUrlForGet}?projectId=${projectId}`);
    }
}

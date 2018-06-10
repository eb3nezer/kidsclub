import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { StudentTeam } from "../model/studentTeam";

@Injectable({
  providedIn: 'root'
})
export class TeamService {
    private meUrlForGet = '/rest/students/teams';
    private meUrlForUpdate = '/rest/students/teams';

    constructor(private http: HttpClient) {
    }

    getTeamObservable(id: number): Observable<StudentTeam> {
        return this.http.get<StudentTeam>(`${this.meUrlForGet}/${id}`);
    }

    getTeamsForProject(projectId: number): Observable<StudentTeam[]> {
        return this.http.get<StudentTeam[]>(`${this.meUrlForGet}?projectId=${projectId}`);
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
        return this.http.post(`${this.meUrlForUpdate}/${team.id}`, formData);
    }

}

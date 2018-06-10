import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {Student} from "../model/student";

@Injectable({
  providedIn: 'root'
})
export class StudentService {
    private meUrlForStudentSearch = '/rest/students';

    constructor(private http: HttpClient) {
    }

    loadStudentMatchingForProject(projectId: number, query: string): Observable<Student[]> {
        return this.http.get<Student[]>(`${this.meUrlForStudentSearch}?projectId=${projectId}&name=${query}`);
    }
}

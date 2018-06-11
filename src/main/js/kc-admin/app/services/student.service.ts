import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import {Student} from "../model/student";
import {HttpErrorService} from "./http-error.service";

@Injectable({
  providedIn: 'root'
})
export class StudentService {
    private studentUrl = '/rest/students';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    loadStudentMatchingForProject(projectId: number, query: string): Observable<Student[]> {
        return this.http.get<Student[]>(`${this.studentUrl}?projectId=${projectId}&name=${query}`)
            .pipe(
                catchError(this.errorService.handleError('Get matching students', []))
            );
    }

    loadStudentsForProject(projectId: number): Observable<Student[]> {
        return this.http.get<Student[]>(`${this.studentUrl}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleError('Get students', []))
        );
    }

    loadStudentById(studentId: number): Observable<Student> {
        return this.http.get<Student>(`${this.studentUrl}/${studentId}`).pipe(
            catchError(this.errorService.handleError('Get student', new Student()))
        );
    }

    updateStudent(student: Student, profilePhoto: File): Observable<Student> {
        const formData: FormData = new FormData();
        for (let property in student) {
            if (student.hasOwnProperty(property)) {
                formData.append(property, student[property]);
            }
        }
        if (student.studentTeam) {
            formData.append("team", student.studentTeam.id.toString());
        }
        if (profilePhoto) {
            formData.append("file", profilePhoto, profilePhoto.name);
        }
        return this.http.post<Student>(`${this.studentUrl}/${student.id}`, formData).pipe(
            catchError(this.errorService.handleError('Update student', student))
        );
    }

    importCSVFile(projectId: number, csvFile: File): Observable<Student[]> {
        const formData: FormData = new FormData();
        formData.append("file", csvFile, csvFile.name);
        formData.append("projectId", projectId.toString());

        return this.http.post<Student[]>(`${this.studentUrl}`, formData).pipe(
            catchError(this.errorService.handleError('Import students', []))
        );
    }

    createStudent(student: Student, projectId: number): Observable<Student> {
        student.projectId = projectId;
        return this.http.put<Student>(this.studentUrl, student).pipe(
            catchError(this.errorService.handleError('Create student', student))
        );
    }

    deleteStudent(student: Student): Observable<Student> {
        return this.http.delete(`${this.studentUrl}/${student.id}`).pipe(
            catchError(this.errorService.handleError('Delete student', undefined))
        );
    }
}

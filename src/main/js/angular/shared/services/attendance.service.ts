import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {catchError} from "rxjs/operators";
import {AttendanceRecord} from "../model/attendanceRecord";
import {Student} from "../model/student";
import {AttendanceDetails} from "../model/attendanceDetails";

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
    private url = '/rest/attendance';

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    updateAttendance(projectId: number, studentId: number, attendanceCode: string, comment: string): Observable<AttendanceDetails> {
        const student = new Student(studentId);
        const attendanceRecord = new AttendanceRecord(undefined, student, attendanceCode, attendanceCode, undefined, undefined, comment);
        return this.http.put<AttendanceRecord>(`${this.url}/project/${projectId}/student/${studentId}`, attendanceRecord).pipe(
            catchError(this.errorService.handleErrorWithDialog('Update attendance', undefined))
        );
    }

    getTodaysAttendanceForProject(projectId: number): Observable<AttendanceDetails> {
        return this.http.get<AttendanceRecord[]>(`${this.url}/project/${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get attendance', undefined))
        );
    }

    getAllAttendanceForStudent(projectId: number, studentId: number): Observable<AttendanceRecord[]> {
        return this.http.get<AttendanceRecord[]>(`${this.url}/project/${projectId}/student/${studentId}/all`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get attendance', []))
        );
    }
}

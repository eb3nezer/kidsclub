import { Injectable } from '@angular/core';
import { AuditRecord } from "../model/auditRecord";
import { Observable, Subscriber, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Project} from "../model/project";

@Injectable({
  providedIn: 'root'
})
export class AuditService {
    private urlForGet = "/rest/audit";

    constructor(private http: HttpClient) {
    }

    getAuditRecordsForProject(projectId: number, count: number, start: number): Observable<AuditRecord[]> {
        return this.http.get<AuditRecord[]>(`${this.urlForGet}?projectId=${projectId}&count=${count}&start=${start}`);
    }
}

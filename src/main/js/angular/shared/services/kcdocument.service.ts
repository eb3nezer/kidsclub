import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {KCDocument} from "../model/kcDocument";

@Injectable({
  providedIn: 'root'
})
export class KcdocumentService {
    url = "/rest/documents";

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    getAllDocumentsForProject(projectId: number): Observable<KCDocument[]> {
        return this.http.get<KCDocument[]>(`${this.url}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Get all documents', []))
        );
    }

    deleteDocument(documentId: number, projectId: number): Observable<any> {
        return this.http.delete(`${this.url}/${documentId}/project/${projectId}`).pipe(
            catchError(this.errorService.handleErrorWithDialog('Delete document', undefined))
        );
    }

    addDocument(projectId: number, description: string, file?: File): Observable<KCDocument> {
        const formData: FormData = new FormData();
        formData.append("description", description);
        if (file) {
            formData.append("file", file, file.name);
        }
        return this.http.post(`${this.url}/upload/${projectId}`, formData).pipe(
            catchError(this.errorService.handleErrorWithDialog('Upload document', undefined))
        );
    }
}

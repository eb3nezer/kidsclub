import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {KCDocument} from "../model/kcDocument";
import {Album} from "../model/album";
import {Project} from "../model/project";

@Injectable({
  providedIn: 'root'
})
export class AlbumService {
    url = "/rest/albums";

    constructor(
        private http: HttpClient,
        private errorService: HttpErrorService) {
    }

    getAllAlbumsForProject(projectId: number): Observable<Album[]> {
        return this.http.get<Album[]>(`${this.url}?projectId=${projectId}`).pipe(
            catchError(this.errorService.handleError('Get all albums', []))
        );
    }

    createAlbum(projectId: number, album: Album): Observable<Album> {
        const project = new Project(projectId);
        album.project = project;
        return this.http.put(this.url, album).pipe(
            catchError(this.errorService.handleError('Create album', undefined))
        );
    }
}

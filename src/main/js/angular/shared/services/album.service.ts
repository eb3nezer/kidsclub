import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import {HttpErrorService} from "./http-error.service";
import {KCDocument} from "../model/kcDocument";
import {Album} from "../model/album";
import {Project} from "../model/project";
import {AlbumItem} from "../model/albumItem";

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

    getAlbum(albumId: number): Observable<Album> {
        return this.http.get<Album>(`${this.url}/${albumId}`).pipe(
            catchError(this.errorService.handleError('Get Album', undefined))
        );
    }

    createAlbum(projectId: number, album: Album): Observable<Album> {
        const project = new Project(projectId);
        album.project = project;
        return this.http.put(this.url, album).pipe(
            catchError(this.errorService.handleError('Create album', undefined))
        );
    }

    uploadPhoto(albumId: number, description: string, fileToUpload: File): Observable<AlbumItem> {
        const formData: FormData = new FormData();
        formData.append("description", description);
        formData.append("shared", "false");
        if (fileToUpload) {
            formData.append("file", fileToUpload, fileToUpload.name);
        }
        return this.http.post<AlbumItem>(`${this.url}/upload/${albumId}`, formData).pipe(
            catchError(this.errorService.handleError('Upload photo', undefined))
        );
    }

    deletePhoto(albumId: number, albumItemId: number): Observable<AlbumItem> {
        return this.http.delete(`${this.url}/${albumId}/photo/${albumItemId}`).pipe(
            catchError(this.errorService.handleError('Upload photo', undefined))
        );
    }
}

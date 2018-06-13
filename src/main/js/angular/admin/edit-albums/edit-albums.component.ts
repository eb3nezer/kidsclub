import { Component, OnInit } from '@angular/core';
import {ProjectService} from "../../shared/services/project.service";
import {ActivatedRoute} from "@angular/router";
import {AppTitleService} from "../../shared/services/app-title.service";
import {Project} from "../../shared/model/project";
import {Location} from "@angular/common";
import {MatSnackBar} from "@angular/material";
import {Album} from "../../shared/model/album";
import {AlbumService} from "../../shared/services/album.service";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {MatDialog} from '@angular/material';

@Component({
  selector: 'app-edit-albums',
  templateUrl: './edit-albums.component.html',
  styleUrls: ['./edit-albums.component.css']
})
export class EditAlbumsComponent implements OnInit {
    displayedColumns = ["name", "description", "photoCount"];
    albums: Album[];
    project: Project;
    newAlbum: Album;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private route: ActivatedRoute,
        private albumService: AlbumService,
        private location: Location,
        private snackBar: MatSnackBar,
        private dialog: MatDialog) {
        this.newAlbum = new Album();
    }

    loadProjectAndDocuments() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Photo albums for ${project.name}`);
                this.appTitleService.setCurrentProject(project);
            });

            this.albumService.getAllAlbumsForProject(projectId).subscribe(next => this.albums = next);
        }
    }

    ngOnInit() {
    }

    onSubmit() {
        if (!this.newAlbum.name) {
            this.dialog.open(ConfirmDialogComponent, {
                data: {
                    title: "Error",
                    text: 'Please give the album a name.',
                    buttons: ["OK"]
                }
            });
        } else {
            this.albumService.createAlbum(this.project.id, this.newAlbum).subscribe(next => {
                this.albums.push(next);
                // Makes the table refresh
                this.albums = this.albums.slice();

                this.snackBar.open('Album created', 'Dismiss', {
                    duration: 10000,
                })
            });
        }
    }

    onCancel() {
        this.location.back();
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndDocuments());
    }
}

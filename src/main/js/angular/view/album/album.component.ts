import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {AlbumService} from "../../shared/services/album.service";
import {Album} from "../../shared/model/album";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {MatSnackBar} from "@angular/material";
import {MatDialog} from '@angular/material';
import {AlbumItem} from "../../shared/model/albumItem";

@Component({
  selector: 'view-album',
  templateUrl: './album.component.html',
  styleUrls: ['./album.component.css']
})
export class AlbumComponent implements OnInit {
    project: Project;
    album: Album;
    description: string;
    newFilename: string;
    fileToUpload: File;

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private albumService: AlbumService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog
    ) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });

            const albumId = +this.route.snapshot.paramMap.get('albumId');

            this.albumService.getAlbum(albumId).subscribe(album => {
                this.album = album;
                this.appTitleService.setTitle(album.name);
            });
        }
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    onSubmit() {
        if (!this.fileToUpload) {
            this.dialog.open(ConfirmDialogComponent, {
                data: {
                    title: "Error",
                    text: 'Please choose a file first.',
                    buttons: ["OK"]
                }
            });
        } else if (!this.description) {
            this.dialog.open(ConfirmDialogComponent, {
                data: {
                    title: "Error",
                    text: 'Please enter a description for the new photo.',
                    buttons: ["OK"]
                }
            });
        } else {
            this.albumService.uploadPhoto(this.album.id, this.description, this.fileToUpload).subscribe(albumItem => {
                if (albumItem) {
                    this.snackBar.open(`Photo added`, 'Dismiss', {
                        duration: 5000,
                    });
                    this.fileToUpload = undefined;
                    this.description = undefined;
                    this.newFilename = undefined;
                    this.album.items.push(albumItem);
                }
            });
        }
    }

    deletePhoto(albumItem: AlbumItem) {
        let dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '250px',
            data: {
                title: "Confirm",
                text: `Are you sure you wish to delete this photo? This operation cannot be undone.`,
                buttons: ["Delete", "Cancel"]
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === "Delete") {
                this.albumService.deletePhoto(this.album.id, albumItem.id).subscribe(() => {
                    let found = false;
                    for (let i = 0; i < this.album.items.length && !found; i++) {
                        if (this.album.items[i].id == albumItem.id) {
                            found = true;
                            this.album.items.splice(i, 1);
                        }
                    }
                    this.snackBar.open(`Photo deleted`, 'Dismiss', {
                        duration: 5000,
                    })
                });
            }
        });
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}

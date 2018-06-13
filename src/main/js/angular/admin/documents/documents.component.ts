import { Component, OnInit } from '@angular/core';
import {KCDocument} from "../../shared/model/kcDocument";
import {ProjectService} from "../../shared/services/project.service";
import {ActivatedRoute} from "@angular/router";
import {AppTitleService} from "../../shared/services/app-title.service";
import {Project} from "../../shared/model/project";
import {KcdocumentService} from "../../shared/services/kcdocument.service";
import {Location} from "@angular/common";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {MatSnackBar} from "@angular/material";
import {MatDialog} from '@angular/material';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.css']
})
export class DocumentsComponent implements OnInit {
    documents: KCDocument[];
    project: Project;
    displayedColumns = ["icon", "updated", "file", "description"];
    newFilename: string;
    fileToUpload: File;
    documentDescription: string;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private route: ActivatedRoute,
        private documentService: KcdocumentService,
        private location: Location,
        private snackBar: MatSnackBar,
        private dialog: MatDialog) {
    }

    loadProjectAndDocuments() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Documents for ${project.name}`)
                this.appTitleService.setCurrentProject(project);
            });

            this.documentService.getAllDocumentsForProject(projectId).subscribe(result => {
                this.documents = result;
            })
        }
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    ngOnInit() {
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
        } else {
            this.documentService.addDocument(this.project.id, this.documentDescription, this.fileToUpload).subscribe(next => {
                this.snackBar.open(`Document uploaded`, 'Dismiss', {
                    duration: 10000,
                });
                this.fileToUpload = undefined;
                this.documentDescription = undefined;
                this.newFilename = undefined;
                this.documents.push(next);
                // Makes the table refresh
                this.documents = this.documents.slice();
            });
        }
    }

    onCancel() {
        this.location.back();
    }

    removeDocument(thisDoc: KCDocument) {
        let dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '250px',
            data: {
                title: "Confirm",
                text: `Are you sure you wish to remove ${thisDoc.name}? This operation cannot be undone.`,
                buttons: ["Delete", "Cancel"]
            }
        });

        const currentProject = this.project;

        dialogRef.afterClosed().subscribe(result => {
            if (result === "Delete") {
                this.documentService.deleteDocument(thisDoc.id, this.project.id).subscribe(() => {
                    let found = false;
                    for (let i = 0; i < this.documents.length && !found; i++) {
                        if (this.documents[i].id == thisDoc.id) {
                            found = true;
                            this.documents.splice(i, 1);
                        }
                    }
                    if (found) {
                        // Makes the table refresh
                        this.documents = this.documents.slice();
                    }
                    this.snackBar.open(`Document ${thisDoc.name} deleted`, 'Dismiss', {
                        duration: 10000,
                    })

                });
            }
        });
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndDocuments());
    }
}

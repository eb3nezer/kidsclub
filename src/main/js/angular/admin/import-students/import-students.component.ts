import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import { Location } from '@angular/common';
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";

import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { StudentService } from "../../shared/services/student.service";
import { Project } from "../../shared/model/project";
import {ConfirmDialogComponent} from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-import-students',
  templateUrl: './import-students.component.html',
  styleUrls: ['./import-students.component.css']
})
export class ImportStudentsComponent implements OnInit {
    project: Project;
    newFilename: string;
    fileToUpload: File;
    importDisabled = false;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private studentService: StudentService,
        private route: ActivatedRoute,
        private location: Location,
        private snackBar: MatSnackBar,
        private router: Router,
        private dialog: MatDialog) {
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    onCancel() {
        this.location.back();
    }

    onSubmit() {
        if (this.fileToUpload) {
            this.importDisabled = true;
            this.studentService.importCSVFile(this.project.id, this.fileToUpload).subscribe(result => {
                this.importDisabled = false;
                this.snackBar.open(`Imported ${result.length} students`, 'Dismiss', {
                    duration: 10000,
                });
                if (result.length > 0) {
                    this.router.navigate(["viewstudents", this.project.id]);
                }
            });
        } else {
            this.dialog.open(ConfirmDialogComponent, {
                data: {
                    title: "Error",
                    text: "Please select a CSV file to upload",
                    buttons: ["OK"]
                }
            });
        }
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Import students for ${project.name}`);
                this.appTitleService.setCurrentProject(project);
            });
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }

}

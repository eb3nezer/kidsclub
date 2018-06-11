import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { StudentService } from "../services/student.service";
import { Project } from "../model/project";
import {ActivatedRoute} from "@angular/router";
import { Location } from '@angular/common';
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-import-students',
  templateUrl: './import-students.component.html',
  styleUrls: ['./import-students.component.css']
})
export class ImportStudentsComponent implements OnInit {
    project: Project;
    newFilename: string;
    fileToUpload: File;


    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private studentService: StudentService,
        private route: ActivatedRoute,
        private location: Location,
        public snackBar: MatSnackBar) {
    }

    private setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    private onCancel() {
        this.location.back();
    }

    private onSubmit() {
        this.studentService.importCSVFile(this.project.id, this.fileToUpload).subscribe(result => {
            this.snackBar.open(`Imported ${result.length} students`, 'Dismiss', {
                duration: 10000,
            })
        });
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
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

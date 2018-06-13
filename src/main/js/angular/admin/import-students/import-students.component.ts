import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { StudentService } from "../../shared/services/student.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute} from "@angular/router";
import { Location } from '@angular/common';
import {MatSnackBar} from "@angular/material";
import {Router} from "@angular/router";

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
        private snackBar: MatSnackBar,
        private router: Router) {
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
        this.studentService.importCSVFile(this.project.id, this.fileToUpload).subscribe(result => {
            this.snackBar.open(`Imported ${result.length} students`, 'Dismiss', {
                duration: 10000,
            });
            if (result.length > 0) {
                this.router.navigate(["viewstudents", this.project.id]);
            }
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

import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material';
import {ActivatedRoute} from "@angular/router";
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { Project } from "../model/project";
import {Student} from "../model/student";
import {StudentService} from "../services/student.service";
import {ConfirmDialogComponent} from "../confirm-dialog/confirm-dialog.component";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-view-students',
  templateUrl: './view-students.component.html',
  styleUrls: ['./view-students.component.css']
})
export class ViewStudentsComponent implements OnInit {
    project: Project;
    students: Student[];
    displayedColumns = ['name', 'warnings', 'team'];

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private studentService: StudentService,
        private route: ActivatedRoute,
        public dialog: MatDialog,
        public snackBar: MatSnackBar) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Students for ${project.name}`)
                this.appTitleService.setCurrentProject(project);
            });

            this.studentService.loadStudentsForProject(projectId).subscribe(students => this.students = students);
        }
    }

    removeStudent(student: Student) {
        let dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '250px',
            data: {
                title: "Confirm",
                text: `Are you sure you wish to delete ${student.name}? This operation cannot be undone.`,
                buttons: ["Delete", "Cancel"]
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === "Delete") {
                this.studentService.deleteStudent(student).subscribe(() => {
                    let found = false;
                    for (let i = 0; i < this.students.length && !found; i++) {
                        if (this.students[i].id == student.id) {
                            found = true;
                            this.students.splice(i, 1);
                        }
                    }
                    this.snackBar.open(`Student ${student.name} deleted`, 'Dismiss', {
                        duration: 10000,
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

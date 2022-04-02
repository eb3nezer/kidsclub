import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";

import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {Student} from "../../shared/model/student";
import {StudentService} from "../../shared/services/student.service";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {UserProfileService} from "../../shared/services/user-profile.service";

@Component({
  selector: 'app-view-students',
  templateUrl: './view-students.component.html',
  styleUrls: ['./view-students.component.css']
})
export class ViewStudentsComponent implements OnInit {
    project: Project;
    students: Student[];
    displayedColumns = ['name', 'warnings', 'team'];
    editStudentsDisabled = true;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private studentService: StudentService,
        private route: ActivatedRoute,
        public dialog: MatDialog,
        public snackBar: MatSnackBar,
        public userProfileService: UserProfileService) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Students for ${project.name}`)
                this.appTitleService.setCurrentProject(project);
            });

            this.studentService.loadStudentsForProject(projectId).subscribe(students => this.students = students);

            this.userProfileService.getMyPermissionsForProject(projectId).subscribe(permissions => {
                if (permissions) {
                    this.editStudentsDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "EDIT_STUDENTS");
                }
            });
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
                    const tempStudents = this.students;
                    let found = false;
                    for (let i = 0; i < this.students.length && !found; i++) {
                        if (tempStudents[i].id == student.id) {
                            found = true;
                            tempStudents.splice(i, 1);
                        }
                    }
                    if (found) {
                        this.students = tempStudents.slice();
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

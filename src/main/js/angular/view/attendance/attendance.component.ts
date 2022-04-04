import {Component, OnInit, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import { Location } from '@angular/common';
import {MatSnackBar} from "@angular/material/snack-bar";
import {Observable} from "rxjs/index";
import {FormControl} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";

import { AppTitleService } from "../../shared/services/app-title.service";
import {ProjectService} from "../../shared/services/project.service";
import {Project} from "../../shared/model/project";
import {AttendanceRecord} from "../../shared/model/attendanceRecord";
import {Student} from "../../shared/model/student";
import {AttendanceService} from "../../shared/services/attendance.service";
import {StudentService} from "../../shared/services/student.service";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {AttendanceCount} from "../../shared/model/attendanceCount";

@Component({
  selector: 'app-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css']
})
export class AttendanceComponent implements OnInit {
    project: Project;
    recentAttendance: AttendanceRecord[];
    student: Student;
    attendanceCode: string;
    comment: string;
    studentSuggestions: Observable<Student[]>;
    studentAutocomplete = new FormControl();
    displayedColumns = ["name", "team", "status", "time", "who"];
    saveDisabled = false;
    attendanceCount: AttendanceCount;
    @ViewChildren('input') inputField;

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private projectService: ProjectService,
        private attendanceService: AttendanceService,
        private snackBar: MatSnackBar,
        private studentService: StudentService,
        private dialog: MatDialog) {
    }

    loadProjectAndAttendance() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                if (project) {
                    this.project = project;
                    this.appTitleService.setTitle("Attendance");
                    this.appTitleService.setCurrentProject(project);

                    this.attendanceService.getTodaysAttendanceForProject(projectId).subscribe(attendanceDetails => {
                        if (attendanceDetails) {
                            this.recentAttendance = attendanceDetails.attendanceRecords;
                            this.attendanceCount = attendanceDetails.attendanceCount;
                        }
                    });
                }
            });
        }
    }

    private studentChosen(event) {
        let found = false;
        this.student = this.studentAutocomplete.value;

        this.studentAutocomplete.setValue(this.student.name);
    }

    onSubmit() {
        if (this.student && this.attendanceCode) {
            this.saveDisabled = true;
            this.attendanceService.updateAttendance(this.project.id, this.student.id, this.attendanceCode, this.comment).subscribe(attendanceDetails => {
                this.saveDisabled = false;
                if (attendanceDetails) {
                    this.attendanceCount = attendanceDetails.attendanceCount;
                    this.snackBar.open(`Attendance updated`, 'Dismiss', {
                        duration: 5000,
                    });
                    this.student = undefined;
                    this.comment = undefined;
                    this.studentAutocomplete.setValue(undefined);
                    this.studentSuggestions = undefined;
                    // Focus the input field
                    this.inputField.first.nativeElement.focus();
                    if (this.recentAttendance.length >= 10) {
                        this.recentAttendance.pop();
                    }
                    this.recentAttendance.unshift(attendanceDetails.attendanceRecords[0]);
                    // Make the table update
                    this.recentAttendance = this.recentAttendance.slice();
                }
            })
        } else {
            if (!this.student) {
                this.dialog.open(ConfirmDialogComponent, {
                    data: {
                        title: "Error",
                        text: 'Please choose a student',
                        buttons: ["OK"]
                    }
                });
            } else if (!this.attendanceCode) {
                this.dialog.open(ConfirmDialogComponent, {
                    data: {
                        title: "Error",
                        text: "Please select an Attendance value",
                        buttons: ["OK"]
                    }
                });
            }
        }
    }

    onCancel() {
        this.location.back();
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndAttendance());
        // Set up suggestions for students
        this.studentAutocomplete.valueChanges.subscribe(
            newValue => {
                if (newValue && newValue.length >= 2) {
                    this.studentSuggestions = this.studentService.loadStudentMatchingForProject(this.project.id, newValue)
                }
            });
    }
}
